import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GaussSeidel {
    private int n; //кол-во уравнений (строк)
    private int m; //кол-во коэффициентов (столбцов)
    private double[][] matrix; //коэффициенты
    private double[] solution;
    private int count; //кол-во итераций
    private final double accuracy = 1E-5;
    private final int monotony = 5;

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
        count = Integer.parseInt(sn[1]);
        solution = new double[n];
        create(n, m);
        int i, j;
        for (i = 0; i < n; i++) {
            str = scan.nextLine();
            sn = pat.split(str);
            for (j = 0; j < m; j++)
                matrix[i][j] = Double.parseDouble(sn[j]);
        }
        scan.close();
    }

    //вывод системы на печать
    public void print() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.printf("%15.6E", matrix[i][j]);
            }
            System.out.println();
        }
    }

    //расчет решения
    public boolean resolve(boolean SCC) {
        System.out.println("Решения:");
        double difference = iterate();
        if (SCC) { //если ДУС выполняется
            while (difference > accuracy) { //выполняем итерации, пока не добьемся нужной точности
                difference = iterate();
            }
            return true;
        } else { //если ДУС не выполняется
            int decrease = 0;
            for (int i = 1; i < count; i++) {
                double newDifference = iterate();
                if (newDifference > difference) { //проверяем монотонное убывание на каждом шаге
                    decrease++;
                } else {
                    decrease = 0;
                }
                if (newDifference < accuracy) { //выходим из цикла, если добились нужной точности
                    return true;
                }
                difference = newDifference;
            }
            if (decrease < monotony) { //если монотонность не замечена
                System.out.println("Метод расходится:");
                return false;
            } else { //если есть монотонность
                while (difference > accuracy) { //выполняем итерации, пока не добьемся нужной точности
                    difference = iterate();
                }
                return true;
            }
        }
    }

    //проверка наличия 0 на главное диагонали
    public boolean checkZero() {
        int i = 0;
        while (matrix[i][i] != 0 && i < n) {
            i++;
        }
        return i < n;
    }

    //проверка матрицы на ДУС
    public boolean checkSCC() {
        boolean strictSCC = false;
        boolean SCC = true;
        int k = 0;
        while (SCC && k < n) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                if (i != k) {
                    sum += matrix[k][i];
                }
            }
            if (matrix[k][k] > sum) strictSCC = true; //выполнился строгий ДУС
            if (sum > matrix[k][k]) SCC = false; //ДУС не выполнился
            k++;
        }
        return strictSCC & SCC;
    }

    //итерация поиска решения
    private double iterate() {
        double[] result = new double[n];
        double maxDifference = Double.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            result[i] = solveVariable(i);
            double difference = abs(result[i] - solution[i]);
            if (difference > maxDifference)
                maxDifference = difference;
        }
        solution = result;
        solution();
        System.out.println();
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

    //привести матрицу к диагональному преобладанию
    public void changeDiagonal(int start) {

    }

    //модуль числа
    private double abs(double a) {
        return a >= 0 ? a : -a;
    }

    //поменять местами строки a и b
    private void swap(int a, int b) {
        if (a != b) {
            double[] temp = matrix[a];
            matrix[a] = matrix[b];
            matrix[b] = temp;
        }
    }

    //вывод решения
    public void solution() {
        for (int i = 0; i < n; i++) {
            System.out.printf("%15.6E", solution[i]);
        }
    }
}
