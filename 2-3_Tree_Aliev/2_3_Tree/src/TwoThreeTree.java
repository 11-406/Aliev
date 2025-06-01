import java.util.ArrayList;
import java.util.List;

public class TwoThreeTree {
    private Node root;
    private final List<Integer> insertOperations = new ArrayList<>();
    private final List<Long> insertTimes = new ArrayList<>();
    private final List<Integer> searchOperations = new ArrayList<>();
    private final List<Long> searchTimes = new ArrayList<>();
    private final List<Integer> deleteOperations = new ArrayList<>();
    private final List<Long> deleteTimes = new ArrayList<>();

    private static class SearchResult {
        Node node;
        int operations;

        SearchResult(Node node, int operations) {
            this.node = node;
            this.operations = operations;
        }
    }

    public boolean search(int key) {
        long startTime = System.nanoTime();
        SearchResult result = search(key, root, 0);
        long elapsed = System.nanoTime() - startTime;

        searchOperations.add(result.operations);
        searchTimes.add(elapsed);

        return result.node != null && result.node.keys.contains(key);
    }

    private SearchResult search(int key, Node node, int ops) {
        ops++;
        if (node == null) {
            return new SearchResult(null, ops);
        }

        for (int i = 0; i < node.keys.size(); i++) {
            ops++;
            if (key == node.keys.get(i)) {
                return new SearchResult(node, ops);
            }
            if (key < node.keys.get(i)) {
                if (node.isLeaf) {
                    return new SearchResult(null, ops);
                }
                return search(key, node.children.get(i), ops);
            }
        }

        if (node.isLeaf) {
            return new SearchResult(null, ops);
        }
        return search(key, node.children.get(node.children.size() - 1), ops);
    }

    public void insert(int key) {
        long startTime = System.nanoTime();
        int ops = 0;

        if (root == null) {
            root = new Node();
            root.keys.add(key);
            ops++;
        } else {
            SearchResult result = search(key, root, 0);
            ops += result.operations;

            if (result.node != null && result.node.keys.contains(key)) {
                long elapsed = System.nanoTime() - startTime;
                insertOperations.add(ops);
                insertTimes.add(elapsed);
                return;
            }

            Node current = root;
            while (!current.isLeaf) {
                ops++;
                int pos = 0;
                while (pos < current.keys.size() && key > current.keys.get(pos)) {
                    pos++;
                    ops++;
                }
                current = current.children.get(pos);
            }

            int pos = 0;
            while (pos < current.keys.size() && key > current.keys.get(pos)) {
                pos++;
                ops++;
            }
            current.keys.add(pos, key);
            ops++;

            if (current.keys.size() > 2) {
                Node newRoot = split(current);
                if (newRoot.parent == null) {
                    root = newRoot;
                    ops++;
                }
            }
        }

        long elapsed = System.nanoTime() - startTime;
        insertOperations.add(ops);
        insertTimes.add(elapsed);
    }

    private Node split(Node node) {
        if (node.keys.size() < 3) return node;

        Node left = new Node();
        left.keys.add(node.keys.get(0));
        left.isLeaf = node.isLeaf;

        Node right = new Node();
        right.keys.add(node.keys.get(2));
        right.isLeaf = node.isLeaf;

        if (!node.isLeaf) {
            left.children.addAll(node.children.subList(0, 2));
            right.children.addAll(node.children.subList(2, 4));
            for (Node child : left.children) child.parent = left;
            for (Node child : right.children) child.parent = right;
        }

        int middleKey = node.keys.get(1);

        if (node.parent == null) {
            Node newRoot = new Node();
            newRoot.keys.add(middleKey);
            newRoot.children.add(left);
            newRoot.children.add(right);
            newRoot.isLeaf = false;
            left.parent = newRoot;
            right.parent = newRoot;
            return newRoot;
        } else {
            Node parent = node.parent;
            left.parent = parent;
            right.parent = parent;

            int pos = 0;
            while (pos < parent.keys.size() && middleKey > parent.keys.get(pos)) {
                pos++;
            }

            parent.keys.add(pos, middleKey);
            parent.children.remove(node);
            parent.children.add(pos, left);
            parent.children.add(pos + 1, right);
            parent.isLeaf = false;

            if (parent.keys.size() > 2) {
                return split(parent);
            }
            return parent;
        }
    }

    public boolean delete(int key) {
        long startTime = System.nanoTime();
        int ops = 0;

        if (root == null) {
            recordDeleteMetrics(ops, startTime);
            return false;
        }

        SearchResult result = search(key, root, 0);
        ops += result.operations;

        if (result.node == null || !result.node.keys.contains(key)) {
            recordDeleteMetrics(ops, startTime);
            return false;
        }

        return deleteNode(result.node, key, ops, startTime);
    }

    private boolean deleteNode(Node node, int key, int ops, long startTime) {
        // Если это внутренний узел, заменяем на преемника
        if (!node.isLeaf) {
            int keyIndex = node.keys.indexOf(key);
            if (keyIndex == -1) {
                recordDeleteMetrics(ops, startTime);
                return false;
            }

            // Находим преемника (минимальный в правом поддереве)
            Node successorNode = findSuccessor(node, keyIndex);
            if (successorNode == null) {
                recordDeleteMetrics(ops, startTime);
                return false;
            }

            int successorKey = findMinKey(successorNode);
            if (!delete(successorKey)) {
                recordDeleteMetrics(ops, startTime);
                return false;
            }

            // Безопасная замена ключа
            if (keyIndex < node.keys.size()) {
                node.keys.set(keyIndex, successorKey);
                recordDeleteMetrics(ops + 1, startTime);
                return true;
            }
            recordDeleteMetrics(ops, startTime);
            return false;
        }

        // Удаление из листа
        return deleteFromLeaf(node, key, ops, startTime);
    }

    private int findMinKey(Node node) {
        if (node == null || node.keys.isEmpty()) {
            throw new IllegalStateException("Invalid node for finding minimum key");
        }
        return node.keys.get(0);
    }

    private Node findSuccessor(Node node, int keyIndex) {
        if (node.children.size() > keyIndex + 1) {
            Node successorNode = node.children.get(keyIndex + 1);
            while (!successorNode.isLeaf && !successorNode.children.isEmpty()) {
                successorNode = successorNode.children.get(0);
            }
            return successorNode;
        }
        return null;
    }


    private boolean deleteFromLeaf(Node node, int key, int ops, long startTime) {
        // Безопасное удаление ключа
        if (!node.keys.remove((Integer) key)) {
            recordDeleteMetrics(ops, startTime);
            return false;
        }
        ops++;

        // Балансировка если нужно
        if (node.keys.isEmpty()) {
            if (node == root) {
                handleEmptyRoot();
            } else {
                balanceAfterDelete(node);
            }
        }

        recordDeleteMetrics(ops, startTime);
        return true;
    }

    private void handleEmptyRoot() {
        if (root.children.isEmpty()) {
            root = null;
        } else {
            root = root.children.get(0);
            root.parent = null;
        }
    }


    private void recordDeleteMetrics(int ops, long startTime) {
        long elapsed = System.nanoTime() - startTime;
        deleteOperations.add(ops);
        deleteTimes.add(elapsed);
    }

    private void balanceAfterDelete(Node node) {
        if (node == null || node.parent == null || !node.keys.isEmpty()) {
            return;
        }

        Node parent = node.parent;
        int nodeIndex = parent.children.indexOf(node);
        if (nodeIndex == -1) return;

        // Попытка занять у соседей
        if (tryBorrowFromSibling(parent, nodeIndex)) {
            return;
        }

        // Если нельзя занять - делаем слияние
        mergeWithSibling(parent, nodeIndex);
    }

    private void performBorrowRight(Node parent, int keyIndex, Node rightSibling) {
        Node node = parent.children.get(keyIndex);

        // 1. Перемещаем ключ из родителя в текущий узел
        node.keys.add(parent.keys.get(keyIndex));

        // 2. Перемещаем минимальный ключ из правого брата в родителя
        parent.keys.set(keyIndex, rightSibling.keys.remove(0));

        // 3. Перемещаем первого ребенка правого брата, если есть
        if (!rightSibling.isLeaf && !rightSibling.children.isEmpty()) {
            Node child = rightSibling.children.remove(0);
            node.children.add(child);
            child.parent = node;
        }
    }

    private void mergeWithSibling(Node parent, int nodeIndex) {
        Node node = parent.children.get(nodeIndex);

        // Определяем с каким братом будем сливаться (предпочитаем левого)
        if (nodeIndex > 0) {
            // Слияние с левым братом
            Node leftSibling = parent.children.get(nodeIndex - 1);
            mergeNodes(parent, nodeIndex - 1, leftSibling, node);
        } else {
            // Слияние с правым братом
            Node rightSibling = parent.children.get(nodeIndex + 1);
            mergeNodes(parent, nodeIndex, node, rightSibling);
        }

        // Если родитель стал пустым и это корень
        if (parent.keys.isEmpty() && parent == root) {
            root = parent.children.get(0);
            root.parent = null;
        }
    }

    private void mergeNodes(Node parent, int keyIndex, Node leftNode, Node rightNode) {
        // 1. Переносим ключ из родителя в левый узел
        leftNode.keys.add(parent.keys.remove(keyIndex));

        // 2. Переносим все ключи из правого узла
        leftNode.keys.addAll(rightNode.keys);

        // 3. Переносим детей из правого узла, если есть
        if (!rightNode.isLeaf) {
            for (Node child : rightNode.children) {
                child.parent = leftNode;
            }
            leftNode.children.addAll(rightNode.children);
        }

        // 4. Удаляем правый узел из родителя
        parent.children.remove(keyIndex + 1);

        // 5. Если родитель стал слишком маленьким, балансируем его
        if (parent.keys.size() < 1 && parent != root) {
            balanceAfterDelete(parent);
        }
    }

    private boolean tryBorrowFromSibling(Node parent, int nodeIndex) {
        // Пытаемся занять у левого брата
        if (nodeIndex > 0) {
            Node leftSibling = parent.children.get(nodeIndex - 1);
            if (leftSibling.keys.size() > 1) {
                performBorrowLeft(parent, nodeIndex - 1, leftSibling);
                return true;
            }
        }

        // Пытаемся занять у правого брата
        if (nodeIndex < parent.children.size() - 1) {
            Node rightSibling = parent.children.get(nodeIndex + 1);
            if (rightSibling.keys.size() > 1) {
                performBorrowRight(parent, nodeIndex, rightSibling);
                return true;
            }
        }

        return false;
    }

    private void performBorrowLeft(Node parent, int keyIndex, Node leftSibling) {
        Node node = parent.children.get(keyIndex + 1);

        // Перемещаем ключ из родителя в текущий узел
        node.keys.add(0, parent.keys.get(keyIndex));

        // Перемещаем максимальный ключ из левого брата в родителя
        parent.keys.set(keyIndex, leftSibling.keys.remove(leftSibling.keys.size() - 1));

        // Перемещаем последнего ребенка левого брата, если есть
        if (!leftSibling.isLeaf && !leftSibling.children.isEmpty()) {
            Node child = leftSibling.children.remove(leftSibling.children.size() - 1);
            node.children.add(0, child);
            child.parent = node;
        }
    }


    // Методы для получения статистики
    public double getAverageInsertOperations() {
        return insertOperations.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public double getAverageInsertTimeNanos() {
        return insertTimes.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public double getAverageSearchOperations() {
        return searchOperations.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public double getAverageSearchTimeNanos() {
        return searchTimes.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public double getAverageDeleteOperations() {
        return deleteOperations.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public double getAverageDeleteTimeNanos() {
        return deleteTimes.stream().mapToLong(Long::longValue).average().orElse(0);
    }
}