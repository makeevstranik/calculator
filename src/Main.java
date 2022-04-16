import java.util.Scanner;

public class Main {
    public static void main (String[] args) {

        Calculator calculator = new Calculator();
        Scanner in = new Scanner(System.in);
        System.out.println("Input string to calculate, to quit press q");
        while(true) {
            String inputValue = in.nextLine();
            if (inputValue.equalsIgnoreCase("q")) break;
            String answer = calculator.calculate(inputValue);
            if(answer != null) System.out.println(answer);
        }
        in.close();
    }
}