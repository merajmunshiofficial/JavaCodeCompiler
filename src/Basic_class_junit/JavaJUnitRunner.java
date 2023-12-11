package Basic_class_junit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JavaJUnitRunner {
    
    static JavaFileWriter javafilewriter = new JavaFileWriter();
	
    public static void main(String[] args) throws Exception {
        // Read Java code and JUnit code from the console
        String javaCode = "public class MyClass {\n" +
                "    public static int add(int a, int b) {\n" +
                "        return a + b;\n" +
                "    }\n" +
                "}";
        
                
			
			String javaFileName = javafilewriter.writeJavaFile(javaCode);
			        
        
        // Compile the Java file
        File javaFile = new File(javaFileName+".java");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, javaFile.getAbsolutePath());
        if (result != 0) {
            throw new RuntimeException("Compilation failed");
        }
        
        String junitCode = "import org.junit.Test;\n" +
                "import static org.junit.Assert.*;\n" +
                "\n" +
                "public class MyTest {\n" +
                "    @Test\n" +
                "    public void testAddition() {\n" +
                "        int result = MyClass.add(2, 3);\n" +
                "        assertEquals(5, result);\n" +
                "    }\n" +
                "}";

        // Compile and load Java code dynamically
        DynamicClassLoader classLoader = new DynamicClassLoader();
        Class<?> clazz = classLoader.loadClassFromString(javaCode);

        // Compile and run JUnit code against the loaded Java class
        DynamicClassLoader junitClassLoader = new DynamicClassLoader();
        Class<?> junitClazz = junitClassLoader.loadClassFromString(junitCode);
        JUnitCore junit = new JUnitCore();
        Result result1 = junit.run(junitClazz);

        // Print the test results
        if (result1.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            for (Failure failure : result1.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
}

class DynamicClassLoader extends ClassLoader {
    public Class<?> loadClassFromString(String code) throws Exception {
        byte[] bytes = compile(code);
        return defineClass(null, bytes, 0, bytes.length);
    }

    private byte[] compile(String code) throws Exception {
        // Use JavaCompiler API to compile code
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        InMemoryJavaFileObject file = new InMemoryJavaFileObject("Main", code);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);
        boolean success = task.call();
        if (!success) {
            StringBuilder sb = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                sb.append(diagnostic.getMessage(null)).append("\n");
            }
            throw new RuntimeException(sb.toString());
        }
        return file.getBytes();
    }
}

class InMemoryJavaFileObject extends SimpleJavaFileObject {
    private final String code;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public InMemoryJavaFileObject(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }

    @Override
    public OutputStream openOutputStream() {
        return out;
    }

    public byte[] getBytes() {
        return out.toByteArray();
    }
}