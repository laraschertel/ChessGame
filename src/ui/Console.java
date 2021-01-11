package ui;

import java.util.Scanner;

public class Console {

        public static int readIntegerFromStdIn(String text) {
            System.out.print(text + " ");
            int x = 0;
            boolean a = true;
            while (a) {
                Scanner myInput = new Scanner(System.in);
                if (myInput.hasNextInt()) {
                    x = myInput.nextInt();
                    a = false;
                    return x;
                }
                else {
                    System.out.println("Invalid input, try again");
                    System.out.print(text);
                    myInput.next();
                }
            }
            return x;
        }

    public static char readCharFromStdin(String text) {

        Scanner input = new Scanner(System.in);
        boolean notEmpty;

        do {
            System.out.println(text);
            if (input.hasNext()) {
                notEmpty = true;
            } else {
                notEmpty = false;
                input.next();
            }
        } while (!(notEmpty));
        return input.nextLine().charAt(0);
    }
}
