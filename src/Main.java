import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        GaussSeidel gaussSeidel = new GaussSeidel();
        try {
            gaussSeidel.init("input.txt");
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
        }

        System.out.println("Система:");
        gaussSeidel.print();

        if (gaussSeidel.areThereZerosOnDiagonal()) {
            if (gaussSeidel.rearrangeRows()) {
                System.out.println("Невозможно решить итерационным методом");
                return;
            }
            else {
                System.out.println("Система после перестановок:");
                gaussSeidel.print();
            }
        }
        if (gaussSeidel.isSCC())
            gaussSeidel.resolve(1E-2);
        else {
            if (gaussSeidel.resolve(1E-2, 10, 2) == null) {
                System.out.println("Метод расходится");
                return;
            }
        }
        System.out.print("Ответ: ");
        gaussSeidel.printSolution();
    }
}
