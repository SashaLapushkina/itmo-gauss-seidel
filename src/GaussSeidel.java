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
    private double[] sums;
    private static final double EPS = 1E-3;
    private double accuracy = 1E-3; //точность
    private int count = 10; //кол-во итераций
    private int limit = 2; //количество итераций для схождения

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
        scan.close();
    }

    //вывод системы на печать
    public void print() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.printf("%15.6E", matrix[getIndex(i)][j]);
            }
            System.out.println();
        }
    }

    //берем индекс i-той строки
    private int getIndex(int i) {
        return order == null ? i : order[i];
    }

    //решить систему
    //2 - невозможно решить
    //1 - метод сходиться
    //0 - найдено решение
    public double[] resolve(int code) {
        switch (code) {
            case 2:
                System.out.println("Невозможно решить итерационным методом");
                break;
            case 1:
                if (resolveWithControl()) {
                    System.out.println("Метод расходится");
                    break;
                }
            case 0:
                return resolveWithoutControl();
        }
        return null;
    }

    //проверяем нули на главной диагонали
    public boolean areThereZerosOnDiagonal() {
        sums = new double[n];
        boolean result = false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sums[i] += Math.abs(matrix[i][j]);
                if (i == j && Math.abs(matrix[getIndex(i)][j]) < EPS)
                    result = true;//если на диагонали 0
            }
        }
        return result;
    }

    //переставить строки
    //0 - выполнена ДУС
    //1 - не выполнена ДУС
    //2 - нет перестановки
    public int rearrangeRows() {
        if (areThereZerosOnDiagonal()) {
            int[] permutation = new int[n];
            boolean[] isFree = new boolean[n];

            for (int i = 0; i < n; i++) {
                isFree[i] = true;
            }

            if (getOrder(0, permutation, isFree)) return 0;
            else return order == null ? 2 : 1;

        } else {
            return isSCC() ? 0 : 1;
        }
    }

    // Перебор всех возможных перестановок строк
    private boolean getOrder(int index, int[] elements, boolean[] free) {
        if (index == n - 1) {
            for (int i = 0; i < n; i++) {
                if (free[i]) {
                    elements[index] = i;
                    break;
                }
            }

            if ((Math.abs(matrix[elements[index]][index]) > EPS)) {
                order = toNewArray(elements);
                return isSCC();
            }
        } else {
            for (int i = 0; i < n; i++) {
                if (free[i] && Math.abs(matrix[i][index]) > EPS) {
                    free[i] = false;
                    elements[index] = i;

                    if (getOrder(index + 1, elements, free)) {
                        return true;
                    }

                    free[i] = true;
                }
            }
        }

        return false;
    }

    //скопировать массив в новый
    private int[] toNewArray(int[] array) {
        int[] newArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        return newArray;
    }

    // Проверка ДУС для матрицы
    public boolean isSCC() {
        boolean isSCC = false;
        for (int i = 0; i < n; i++) {
            switch (isRowSCC(getIndex(i), i)) {
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
        double element = Math.abs(matrix[rowId][diagonalId]);

        if (element > sums[rowId] - element + EPS) {
            return 0;                                   // ДУС выполнена в полном объёме для строки (>)
        } else if (element > sums[rowId] - element - EPS) {
            return 1;                                   // ДУС выполнена в неполном объёме для строки (>=)
        } else {
            return 2;                                   // ДУС невыполнена для строки (<)
        }
    }

    //расчет решения
    public double[] resolveWithoutControl() {
        double difference;
        do {
            difference = iterate();
        } while (difference >= accuracy);
        return solution;
    }

    //расчет решения, ограниченный кол-вом итераций
    //true - метод расходится
    //false - метод сходится
    public boolean resolveWithControl() {
        int decreaseCount = 0;
        double difference = iterate();

        for (int i = 1; i < count; i++) {

            double newDifference = iterate();

            if (newDifference < difference) //проверяем монотонное убывание на каждом шаге
                decreaseCount++;
            else
                decreaseCount = 0;
            if (newDifference < accuracy) { //выходим из цикла, если добились нужной точности
                return false;
            }
            difference = newDifference;
        }
        return decreaseCount < limit;
    }

    //итерация поиска решения
    private double iterate() {
        double[] result = new double[n];
        double maxDifference = Double.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            result[i] = solveVariable(i);
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
                sum += matrix[getIndex(index)][i] * solution[i];
        }
        return (matrix[getIndex(index)][m - 1] - sum) / matrix[getIndex(index)][index];
    }

    //вывод решения
    public void printSolution() {
        System.out.println("Ответ:");
        for (int i = 0; i < n; i++) {
            System.out.printf("%15.6E", solution[i]);
        }
    }
}
