package Basic_class;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class JavaJUnitConverter {
    public static void main(String[] args) throws IOException {
BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("Enter Java code:");
        String javaCode = reader.readLine();
        
        System.out.println("Enter JUnit test code:");
        String junitCode = reader.readLine();
        
        
        String javaClass = convertToJavaClass(javaCode, junitCode);
        System.out.println(javaClass);
        
        reader.close();
    }
    
    public static String convertToJavaClass(String javaCode, String junitCode) {
        StringBuilder javaClassBuilder = new StringBuilder();
        
        // Add package declaration, if any
        int packageEndIndex = javaCode.indexOf(";");
        if (packageEndIndex != -1) {
            String packageName = javaCode.substring(0, packageEndIndex + 1);
            javaClassBuilder.append(packageName).append("\n");
            javaCode = javaCode.substring(packageEndIndex + 1).trim();
        }
        
        // Add import statements, if any
        while (javaCode.startsWith("import")) {
            int importEndIndex = javaCode.indexOf(";");
            String importStatement = javaCode.substring(0, importEndIndex + 1);
            javaClassBuilder.append(importStatement).append("\n");
            javaCode = javaCode.substring(importEndIndex + 1).trim();
        }
        
        // Add class declaration
        int classStartIndex = javaCode.indexOf("class");
        int classEndIndex = javaCode.indexOf("{", classStartIndex);
        String classDeclaration = javaCode.substring(classStartIndex, classEndIndex + 1);
        javaClassBuilder.append(classDeclaration).append("\n");
        
        // Add JUnit test method as a regular method in the class
        int testMethodStartIndex = junitCode.indexOf("@Test");
        int testMethodEndIndex = junitCode.indexOf("{", testMethodStartIndex);
        String testMethodDeclaration = junitCode.substring(testMethodStartIndex, testMethodEndIndex + 1);
        int methodNameStartIndex = junitCode.indexOf("test", testMethodStartIndex) + "test".length();
        int methodNameEndIndex = junitCode.indexOf("(", methodNameStartIndex);
        String testMethodName = junitCode.substring(methodNameStartIndex, methodNameEndIndex);
        String testMethodBody = junitCode.substring(testMethodEndIndex + 1, junitCode.lastIndexOf("}")).trim();
        String regularMethodDeclaration = "public void " + testMethodName + "() {\n" + testMethodBody + "\n}\n";
        javaClassBuilder.append(regularMethodDeclaration).append("\n");
        
        // Close class declaration
        javaClassBuilder.append("}");
        
        return javaClassBuilder.toString();
    }
}
