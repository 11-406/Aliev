import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Timsort {

    private static final int MIN_MERGE = 32;
    private static long iterations = 0;

    public static void sort(int[] arr) {
        if (arr == null || arr.length < 2) return;

        int n = arr.length;
        iterations = 0;

        if (n < MIN_MERGE) {
            insertionSort(arr, 0, n);
            return;
        }

        int minRun = minRunLength(n);

        // Сортировка маленьких серий
        for (int i = 0; i < n; i += minRun) {
            int end = Math.min(i + minRun, n);
            insertionSort(arr, i, end);
        }

        // Слияние серий
        Stack<Run> stack = new Stack<>();
        for (int i = 0; i < n; ) {
            int runLen = countRun(arr, i);
            stack.push(new Run(i, runLen));
            i += runLen;
            mergeCollapse(arr, stack);
        }

        // Финализация слияния
        while (stack.size() > 1) {
            Run run2 = stack.pop();
            Run run1 = stack.pop();
            merge(arr, run1.start, run2.start, run2.start + run2.length);
            stack.push(new Run(run1.start, run1.length + run2.length));
        }
    }

    private static int minRunLength(int n) {
        int r = 0;
        while (n >= MIN_MERGE) {
            r |= (n & 1);
            n >>= 1;
            iterations++;
        }
        return n + r;
    }

    private static int countRun(int[] arr, int start) {
        int n = arr.length;
        if (start >= n - 1) return 1;

        int i = start;
        boolean ascending = arr[i] <= arr[i + 1];

        while (i < n - 1) {
            iterations++;
            if ((ascending && arr[i] > arr[i + 1]) || (!ascending && arr[i] < arr[i + 1])) {
                break;
            }
            i++;
        }

        if (!ascending) {
            reverse(arr, start, i);
        }

        return i - start + 1;
    }

    private static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
            iterations++;
        }
    }

    private static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i < right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
                iterations++;
            }
            arr[j + 1] = key;
            iterations++;
        }
    }

    private static void merge(int[] arr, int left, int mid, int right) {
        if (mid >= right) return; // Нечего сливать
        right = Math.min(right, arr.length); // Защита от выхода за границы

        int[] leftArr = Arrays.copyOfRange(arr, left, mid);
        int[] rightArr = Arrays.copyOfRange(arr, mid, right);

        int i = 0, j = 0, k = left;
        while (i < leftArr.length && j < rightArr.length) {
            if (leftArr[i] <= rightArr[j]) {
                arr[k++] = leftArr[i++];
            } else {
                arr[k++] = rightArr[j++];
            }
            iterations++;
        }

        while (i < leftArr.length) {
            arr[k++] = leftArr[i++];
            iterations++;
        }
        while (j < rightArr.length) {
            arr[k++] = rightArr[j++];
            iterations++;
        }
    }

    private static void mergeCollapse(int[] arr, Stack<Run> stack) {
        while (stack.size() > 1) {
            Run run1 = stack.pop();
            Run run2 = stack.pop();

            if (run2.start + run2.length != run1.start) {
                stack.push(run2);
                stack.push(run1);
                break;
            }

            merge(arr, run2.start, run1.start, run1.start + run1.length);
            stack.push(new Run(run2.start, run2.length + run1.length));
        }
    }

    private static class Run {
        int start;
        int length;

        Run(int start, int length) {
            this.start = start;
            this.length = length;
        }
    }

    private static void generateTestData(String filename, int numArrays, int minSize, int maxSize) throws IOException {
        Random random = new Random();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (int i = 0; i < numArrays; i++) {
                int size = minSize + random.nextInt(maxSize - minSize + 1);
                writer.print(size + ":");
                for (int j = 0; j < size; j++) {
                    writer.print(random.nextInt(10000) + (j < size - 1 ? "," : ""));
                }
                writer.println();
            }
        }
    }

    private static List<int[]> readTestData(String filename) throws IOException {
        List<int[]> arrays = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                int size = Integer.parseInt(parts[0]);
                String[] elements = parts[1].split(",");
                int[] arr = new int[size];
                for (int i = 0; i < size; i++) {
                    arr[i] = Integer.parseInt(elements[i]);
                }
                arrays.add(arr);
            }
        }
        return arrays;
    }

    public static void main(String[] args) throws IOException {
        String filename = "test_data.txt";
        int numArrays = 100; // 50-100 массивов
        int minSize = 100;
        int maxSize = 10000;

        // Генерация данных
        generateTestData(filename, numArrays, minSize, maxSize);

        // Чтение данных
        List<int[]> testArrays = readTestData(filename);

        // Сортировка результатов по размеру массивов
        testArrays.sort(Comparator.comparingInt(a -> a.length));

        // Замер времени и вывод
        System.out.println("Размер | Время (мс) | Итерации");
        System.out.println("-----------------------------");
        for (int[] arr : testArrays) {
            long startTime = System.nanoTime();
            sort(arr);
            long endTime = System.nanoTime();
            long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            System.out.printf("%5d | %9d | %9d%n",
                    arr.length, durationMs, iterations);
        }
    }
}