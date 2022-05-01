import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GaussSeidel {
    private int n; //кол-во уравнений (строк)
    private int m; //кол-во коэффициентов (столбцов)
    private double[][] matrix; //коэффициенты
    private double[] solution;
    private int[] order;
    private static final double EPS = 1E-3;

    //выделение памяти под массив
    private void create(int n, int m) {
        matrix = new double[n][];
        for (int i = 0; i < n; i++)
            matrix[i] = new double[m];
    }

    //инициализация массива данными из файла
    public void init(String s) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(s));
        Pattern pat = Pattern.compile("[\\s\\t]+");
        String str = scan.nextLine();
        String[] sn = pat.split(str);
        n = Integer.parseInt(sn[0]);
        m = n + 1;
        solution = new double[n];
        create(n, m);
        for (int i = 0; i < n; i++) {
            str = scan.nextLine();
            sn = pat.split(str);
            for (int j = 0; j < m; j++)
                matrix[i][j] = Double.parseDouble(sn[j]);
        }
        order = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        scan.close();
    }

    private double get(int i, int j){
        return matrix[order[i]][j];
    }

    //вывод системы на печать
    public void print() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.printf("%15.6E", get(i ,j));
            }
            System.out.println();
        }
    }

    //проверяем нули на главной диагонали
    public boolean areThereZerosOnDiagonal() {
        for (int i = 0; i < n; i++) {
            if (Math.abs(get(i ,i)) < EPS) //если на диагонали 0
                return true;
        }
        return false;
    }

    //переставить строки
    public boolean rearrangeRows() {
        int[] permutation = new int[n];
        for (int i = 0; i < n; i++) {
            permutation[i] = i;
        }

        allPermutationRecursive(0, permutation);
        return areThereZerosOnDiagonal();
    }

    // Перебор всех возможных перестановок строк
    private boolean allPermutationRecursive(int index, int[] elements) {
        if (index == n - 1) {
            if (!(Math.abs(matrix[elements[index]][index]) < EPS)) {
                order = elements;
                return isSCC();
            }
        } else {
            for (int i = index + 1; i < n; i++) {
                if (Math.abs(matrix[elements[index]][index]) < EPS) {
                    swap(elements, index, i);
                    continue;
                }

                if (allPermutationRecursive(index + 1, elements)) {
                    return true;
                }

                swap(elements, index, i);
            }

            if (!(Math.abs(matrix[elements[index]][index]) < EPS)) {
                return allPermutationRecursive(index + 1, elements);
            }
        }

        return false;
    }

    private void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    // Проверка ДУС для матрицы
    public boolean isSCC() {
        boolean isSCC = false;
        for (int i = 0; i < n; i++) {
            switch (isRowSCC(order[i], i)) {
                case 0:
                    isSCC = true;
                case 1:
                    break;
                case 2:
                    return false;
            }
        }
        return isSCC;
    }

    // Проверка достаточного условия сходимости для строки
    private int isRowSCC(int rowId, int diagonalId) {
        double mainElement = 0.0;
        double allElement = 0.0;

        for (int i = 0; i < n; i++) {
            if (i == diagonalId) {
                mainElement += Math.abs(matrix[rowId][i]);
            } else {
                allElement += Math.abs(matrix[rowId][i]);
            }
        }

        if (mainElement > allElement + EPS) {
            return 0;                                   // ДУС выполнена в полном объёме для строки (>)
        } else if (mainElement > allElement - EPS) {
            return 1;                                   // ДУС выполнена в неполном объёме для строки (>=)
        } else {
            return 2;                                   // ДУС невыполнена для строки (<)
        }
    }

    //расчет решения
    public double[] resolve(double accuracy) {
        double difference = iterate();
        while (difference > accuracy) {
            difference = iterate();
        }
        return solution;
    }

    public double[] resolve(double accuracy, int count, int limit) {
        int decreaseCount = 0;
        double difference = iterate();
        for (int i = 1; i < count; i++) {
            double newDifference = iterate();
            if (newDifference < difference) //проверяем монотонное убывание на каждом шаге
                decreaseCount++;
            else
                decreaseCount = 0;
            if (newDifference < accuracy) { //выходим из цикла, если добились нужной точности
                return solution;
            }
            difference = newDifference;
        }
        if (decreaseCount < limit) return null;
        else return resolve(accuracy);
    }

    //итерация поиска решения
    private double iterate() {
        double[] result = new double[n];
        double maxDifference = Double.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            result[i] = solveVariable(order[i]);
            double difference = Math.abs(result[i] - solution[i]);
            if (difference > maxDifference)
                maxDifference = difference;
        }
        solution = result;
        return maxDifference;
    }

    //выразить переменную
    private double solveVariable(int index) {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            if (i != index)
                sum += matrix[index][i] * solution[i];
        }
        return (matrix[index][m - 1] - sum) / matrix[index][index];
    }

    //вывод решения
    public void printSolution() {
        for (int i = 0; i < n; i++) {
            System.out.printf("%15.6E", solution[i]);
        }
    }
}
