public class Example {
   public static void main(String[] args) {
       System.out.println("Hello, World!");
   }
}import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleTest {
   @Test
   public void testMain() {
       Example.main(new String[]{});
       assertEquals("Hello, World!", outContent.toString().trim());
   }
}