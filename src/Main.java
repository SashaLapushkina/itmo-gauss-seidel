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

        gaussSeidel.printSolution(gaussSeidel.resolve(1E-2, 10, 2));
    }
}
