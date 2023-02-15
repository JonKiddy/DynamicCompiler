import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class DynamicCompiler {
    private final JavaCompiler compiler;
    private final StandardJavaFileManager fileManager;

    public DynamicCompiler(ClassLoader classLoader) {
        compiler = ToolProvider.getSystemJavaCompiler();
        fileManager = compiler.getStandardFileManager(null, null, null);
    }

    public Class<?> compile(String className, String code) throws IOException, ClassNotFoundException {
        JavaFileObject source = new JavaSourceFromString(className, code);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(source);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        boolean success = task.call();
        if (!success) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
            }
            throw new RuntimeException("Compilation failed.");
        }
        return Class.forName(className);
    }

    private static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
