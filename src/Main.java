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

        int code = gaussSeidel.rearrangeRows();

        System.out.println();
        System.out.println("Система после перестановок:");
        gaussSeidel.print();

        if (gaussSeidel.resolve(code) != null) gaussSeidel.printSolution();
    }
}
