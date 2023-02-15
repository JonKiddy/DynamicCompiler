import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String filename = "MyClass.java"; // the name of the file that contains the class
        String classname = "MyClass"; // the name of the class that we want to instantiate

        // Read the contents of the file
        StringBuilder code = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line).append('\n');
            }
        }

        // Compile the code and load the class
        ClassLoader classLoader = Main.class.getClassLoader();
        DynamicCompiler compiler = new DynamicCompiler(classLoader);
        Class<?> clazz = compiler.compile(classname, code.toString());

        // Instantiate the class
        Constructor<?> constructor = clazz.getConstructor();
        Object obj = constructor.newInstance();

        // Call a method on the object (example)
        clazz.getMethod("sayHello").invoke(obj);
    }
}
