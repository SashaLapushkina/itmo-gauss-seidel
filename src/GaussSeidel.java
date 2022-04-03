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
    private final double accuracy = 1E-1;
    private boolean SCC = true;

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
    public int resolve() {
        System.out.println("Решения:");
        double difference = iterate();
        for (int i = 1; i < count; i++) {
            double newDifference = iterate();
            if (newDifference > difference) { //проверяем монотонное убывание на каждом шаге (?)
                return 2;
            }
            if (newDifference < accuracy) { //выходим из цикла, если добились нужной точности (?)
                return 0;
            }
            difference = newDifference;
        }
        while (SCC && difference > accuracy) {
            difference = iterate();
        }
        return 0;
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
    private double solveVariable(int index){
        double sum = 0;
        for (int i = 0; i < n; i++) {
            if (i != index)
                sum += matrix[index][i] * solution[i];
        }
        return (matrix[index][m - 1] - sum) / matrix[index][index];
    }

    //привести матрицу к диагональному преобладанию
    public int diagonal() {
        for (int i = 0; i < n; i++) { //сверху вниз
            if (matrix[i][i] == 0) { //если на диагонали 0
                int row = findTransposition(i);
                if (row == -1) return 1;
                else swap(i, row);
            }
        }
        return 0;
    }

    //найти перестановку в соответствии с ДУС
    private int findTransposition(int start) {
        int flag = -1;
        for (int i = start; i < n; i++) {
            if (matrix[i][start] != 0) {
                if (checkSCC(i, start))
                    return i;
                else flag = i;
            }
        }
        SCC = false;
        return flag;
    }

    //проверить строку на ДУС
    private boolean checkSCC(int row, int main) {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            if (i != main) {
                if (matrix[row][i] > matrix[row][main])
                    return false;
                else sum += matrix[row][i];
            }
        }
        return matrix[row][main] >= sum;
    }

    //модуль числа
    private double abs(double a) {
        return a >= 0 ? a : -a;
    }

    //поменять местами строки a и b
    private void swap(int a, int b) {
        if (a != b)
            for (int i = 0; i < m; i++) {
                double temp = matrix[a][i];
                matrix[a][i] = matrix[b][i];
                matrix[b][i] = temp;
            }
    }

    //вывод решения
    public void solution() {
        for (int i = 0; i < n; i++) {
            System.out.printf("%15.6E", solution[i]);
        }
    }
}
