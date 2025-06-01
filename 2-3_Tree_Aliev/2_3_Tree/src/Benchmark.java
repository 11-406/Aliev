import java.util.Random;

public class Benchmark {
    public static void run() {
        TwoThreeTree tree = new TwoThreeTree();
        Random random = new Random();

        // 1. Генерация массива из 10000 случайных чисел
        int[] numbers = generateNumbers(10000, random);

        // 2. Тестирование вставки
        testInsert(tree, numbers);

        // 3. Тестирование поиска (100 случайных элементов)
        testSearch(tree, numbers, 100, random);

        // 4. Тестирование удаления (1000 случайных элементов)
        testDelete(tree, numbers, 1000, random);

        // 5. Вывод результатов
        printResults(tree);
    }

    private static int[] generateNumbers(int count, Random random) {
        int[] numbers = new int[count];
        for (int i = 0; i < count; i++) {
            numbers[i] = random.nextInt(100000);
        }
        return numbers;
    }

    private static void testInsert(TwoThreeTree tree, int[] numbers) {
        for (int num : numbers) {
            tree.insert(num);
        }
    }

    private static void testSearch(TwoThreeTree tree, int[] numbers, int count, Random random) {
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(numbers.length);
            tree.search(numbers[index]);
        }
    }

    private static void testDelete(TwoThreeTree tree, int[] numbers, int count, Random random) {
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(numbers.length);
            tree.delete(numbers[index]);
        }
    }

    private static void printResults(TwoThreeTree tree) {
        System.out.println("=== Результаты тестирования 2-3 дерева ===");
        System.out.printf("Среднее количество операций при вставке: %.2f\n", tree.getAverageInsertOperations());
        System.out.printf("Среднее время вставки (нс): %.2f\n", tree.getAverageInsertTimeNanos());
        System.out.printf("Среднее количество операций при поиске: %.2f\n", tree.getAverageSearchOperations());
        System.out.printf("Среднее время поиска (нс): %.2f\n", tree.getAverageSearchTimeNanos());
        System.out.printf("Среднее количество операций при удалении: %.2f\n", tree.getAverageDeleteOperations());
        System.out.printf("Среднее время удаления (нс): %.2f\n", tree.getAverageDeleteTimeNanos());
    }
}