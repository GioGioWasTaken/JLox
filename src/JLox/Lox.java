package JLox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
public class Lox {
    static boolean hadError = false; // Keep track of errors in the code
    private static final Interpreter interpreter = new Interpreter();
    // we want to use the same interpreter in a REPL session, not create a new one for each line.
    static boolean hadRuntimeError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        // Indicate an error in the exit code.
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }
    private static void runPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false; // REPL shouldn't exit upon error
        }
    }
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        // Stop if there was a syntax error.
        if (hadError) return;
        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }
    static void report(int line, String where, String message) { // where will be set in the future
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]"); //finally the line property is useful.
        hadRuntimeError = true;
    }






}
