package JLox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static JLox.TokenType.*; // though it may be a bit crude, In order to prevent writing TokenType everywhere...
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    Scanner(String source) {
        this.source = source; // take the source file
    }

    List<Token> scanTokens() {
    while (!isAtEnd()) {
        // We are at the beginning of the next lexeme.
        start = current;
        scanToken();
    }
    tokens.add(new Token(EOF, "", null, line));
    return tokens;
    }
    private boolean isAtEnd() { // helper function to scanTokens
        return current >= source.length();
    }
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG); // if the next character is... else take only the first char as a token.
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')){
                while ( peek() != '\n' && !isAtEnd() ) advance();
                }
                else addToken(SLASH);
            break;
            case ' ':
            case '\r':
            case '\t':
            // Ignore whitespace.
                break;
            case '\n':
                line++; // keep track of current line each time a new line is encountered.
                break;
            default:
                Lox.error(line, "Unexpected character detected."); // we notify the user about the error, and continue scanning for more errors.
                break;
        }
    }

    private char advance() {
        return source.charAt(current++); // get current char and prepare the next one
    }
    private void addToken(TokenType type) { //overloaded method to handle literals
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++; // the current char has been evaluated, so increment current.
        return true;
    }
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current); // if the current character is \n it will return it and the tokenizer will stop advancing
    }





}

