package Basic_class;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class DynamicClassloaderExample {

    public static void main(String[] args) {
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        	System.out.print("Enter Java code:\n");
        	String javaCode = "";
        	System.out.print("Enter JUnit test code:\n");
        	String testCode = "";
        	try {
        	    String line;
        	    boolean isJavaCode = true;
        	    while ((line = reader.readLine()) != null && !line.isEmpty()) {
        	        if (isJavaCode) {
        	            javaCode += line + "\n";
        	            isJavaCode = false;
        	            System.out.print("Enter JUnit test code:\n");
        	        } else {
        	            testCode += line + "\n";
        	            isJavaCode = true;
        	            System.out.print("Enter Java code:\n");
        	        }
        	    }
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	reader.close();

            // Compile the Java code and the JUnit test code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            String[] options = new String[] { "-d", "out/production/DynamicClassloaderExample" };
            Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(new JavaSourceFromString("MyClass", javaCode), new JavaSourceFromString("MyClassTest", testCode));
            compiler.getTask(null, fileManager, null, Arrays.asList(options), null, compilationUnits).call();

            // Load the target class and the JUnit test class using their binary names
            Class<?> loadedMyClass = new DynamicClassLoader().loadClass("MyClass");
            Class<?> loadedTestClass = new DynamicClassLoader().loadClass("MyClassTest");

            // Run the JUnit tests using JUnitCore
            JUnitCore junit = new JUnitCore();
            junit.addListener(new TextListener(System.out));
            Result result = junit.run(loadedTestClass);

            System.out.println(result.getRunCount() + " tests ran in " + result.getRunTime() + "ms");
            System.out.println(result.getFailureCount() + " tests failed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class JavaSourceFromString extends SimpleJavaFileObject {

    private final String code;

    public JavaSourceFromString(String className, String code) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}

class DynamicClassLoader extends ClassLoader {

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            // Load the class bytes from the compiled file
            byte[] bytes = getClassBytes(name);

            // Define the class using the class bytes
            Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
            if (clazz == null) {
                throw new ClassNotFoundException(name);
            }

            return clazz;
        } catch (Exception e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    private byte[] getClassBytes(String name) throws Exception {
        // Read the class bytes from the compiled file
        String classFileName = name.replace('.', '/') + ".class";
        byte[] bytes = null;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(classFileName)) {
            if (inputStream == null) {
                throw new ClassNotFoundException("Class " + name + " not found.");
            }
            List<String> lines = new ArrayList<>();
            int nextByte;
            while ((nextByte = inputStream.read()) != -1) {
                lines.add(String.format("%02X", nextByte));
            }
            bytes = new byte[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                bytes[i] = (byte) Integer.parseInt(lines.get(i), 16);
            }
        }
        return bytes;
    }

}
