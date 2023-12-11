package Basic_class;

import java.io.*;
import java.util.Scanner;

public class WriteToFile {
    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);

        System.out.println("Enter multiple lines of text:");

        // Read multiple lines of text until an empty line is entered
        StringBuilder sb = new StringBuilder();
        String line = scanner.nextLine();
        while (!line.trim().isEmpty()) { // check for trimmed line being empty
            sb.append(line);
            sb.append(System.lineSeparator());
            line = scanner.nextLine();
        }

        // Print the input
        System.out.println("Input:");
        System.out.println(sb.toString());
    }
}