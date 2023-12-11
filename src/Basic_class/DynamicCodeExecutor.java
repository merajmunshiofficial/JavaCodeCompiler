package Basic_class;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class DynamicCodeExecutor {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String code = "public class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        String className = "HelloWorld";
        String fileName = className + ".java";
        String outputDir = "#Compiled_Class";
        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath)) {
            Files.createDirectory(outputPath);
        }
        Path path = Paths.get(fileName);
        Files.write(path, Arrays.asList(code.split("\n")));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, "-d", outputDir, "-cp", outputDir, fileName);
        if (result == 0) {
            try {
                ClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File(outputDir).toURI().toURL() });
                Class<?> clazz = classLoader.loadClass(className);
                clazz.getMethod("main", String[].class).invoke(null, (Object) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Files.delete(path);
    }
}
