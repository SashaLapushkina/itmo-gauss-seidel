import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        GaussSeidel gaussSeidel = new GaussSeidel();
        try
        {
            gaussSeidel.init("input.txt");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File Not Found!");
        }

        System.out.println("Система:");
        gaussSeidel.print();
        int code = gaussSeidel.diagonal();
        if (code == 1) System.out.println("Невозможно решить итерационным методом");
        else {
            System.out.println("Система после перестановок:");
            gaussSeidel.print();
            code = gaussSeidel.resolve();
            if (code == 2) System.out.println("Метод расходится");
            else {
                System.out.print("Ответ: ");
                gaussSeidel.solution();
            }
        }
    }
}
