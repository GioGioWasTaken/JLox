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

            case '"': string(); break;

            default:
                if(isDigit(c)){
                    number();
                }
                else if(isAlpha(c)){
                    identifier();
                }
                else {
                    Lox.error(line, "Unexpected character detected."); // we notify the user about the error, and continue scanning for more errors.
                }
                break;

        }
    }

    // boolean checks
    private boolean isAlpha(char c) {
        return c>='a' && c<='z' || c>='A' && c<='Z' ||  c == '_'; // we add the _ character since identifiers can also have _
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }


    // token related methods
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
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


    // methods for literals

    private void number() {
        while (isDigit(peek())) advance();
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current))); // we use double type to represent numbers
    }



    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        // Continue until the closing " is detected.
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        } // if it's not detected, return an unterminated string error.

        advance(); // once finished continue parsing characters...

        // Trim the surrounding quotes. Start of the current token, all the way to the end of the current token.
        // (Start is set to the point where the parsing for the current token was started, Line 20.)
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text); // get the value of the key
        if (type == null) type = IDENTIFIER; // if it's not within these values, then it is an identifier.
        addToken(type);
    }

    // keywords map

    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }







}

