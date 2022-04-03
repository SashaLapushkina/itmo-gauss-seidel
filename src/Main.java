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
        boolean zero = gaussSeidel.checkZero();
        if (zero) {
            gaussSeidel.changeDiagonal(0);
            System.out.println("Измененная система:");
            gaussSeidel.print();
        } else {

        }
        gaussSeidel.resolve(zero);
    }
}
