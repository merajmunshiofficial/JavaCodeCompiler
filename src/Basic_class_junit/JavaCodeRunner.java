package Basic_class_junit;

import java.io.*;
import java.lang.reflect.*;
import org.junit.runner.*;
import org.junit.runner.notification.*;

public class JavaCodeRunner {
    public static void main(String[] args) throws Exception {
        // Example Java code as a string
        String code = "public class MyClass {\n" +
                      "  public int add(int a, int b) {\n" +
                      "    return a + b;\n" +
                      "  }\n" +
                      "}";

        // Extract class name from code
        String className = extractClassName(code);

        // Write code to a .java file
        String fileName = className + ".java";
        writeToFile(code, fileName);

        // Compile the .java file to a .class file
        compileFile(fileName);

        // Run JUnit test cases against the compiled class
        Class<?> clazz = Class.forName(className);
        Result result = JUnitCore.runClasses(clazz);

        // Print test results
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println("Tests passed: " + result.wasSuccessful());
    }

    // Extracts class name from a Java code string
    private static String extractClassName(String code) {
        int start = code.indexOf("class ") + 6;
        int end = code.indexOf("{", start);
        return code.substring(start, end).trim();
    }

    // Writes code to a file
    private static void writeToFile(String code, String fileName) throws IOException {
        String filePath = "./" + fileName; // Relative file path
        FileWriter writer = new FileWriter(filePath);
        writer.write(code);
        writer.close();
    }


    // Compiles a Java file
    private static void compileFile(String fileName) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("javac " + fileName);
        process.waitFor();
    }
}
