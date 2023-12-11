package Basic_class_junit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JavaFileWriter {
    
    public static String writeJavaFile(String javaCode) throws IOException {
        
        // Find the position of the "class" keyword in the Java code
        int classKeywordIndex = javaCode.indexOf("class");
        
        // Find the position of the first space after the "class" keyword
        int classNameStartIndex = classKeywordIndex + 6; // "class ".length() == 6
        int classNameEndIndex = javaCode.indexOf(" ", classNameStartIndex);
        if (classNameEndIndex == -1) {
            classNameEndIndex = javaCode.indexOf("{", classNameStartIndex);
        }
        
        // Extract the class name from the Java code
        String className = javaCode.substring(classNameStartIndex, classNameEndIndex);
        
        // Create a File object with the class name and .java extension in the current directory
        File file = new File(className + ".java");
        
        // Create a FileWriter object to write to the file
        FileWriter writer = new FileWriter(file);
        
        // Write the Java code to the file
        writer.write(javaCode);
        
        // Close the FileWriter object
        writer.close();
        
        System.out.println("Java file written successfully!");
        return className ;
    }
    
//    public static void main(String[] args) {
//        String javaCode = "public class HelloWorld {\n" +
//                          "    public static void main(String[] args) {\n" +
//                          "        System.out.println(\"Hello, world!\");\n" +
//                          "    }\n" +
//                          "}";
//        
//        try {
//            writeJavaFile(javaCode);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
