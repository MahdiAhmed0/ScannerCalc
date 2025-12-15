import java.io.IOException;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;


public class ScannerCalc {

    // ===== Character =====
    static final int SPACE = 0;
    static final int NEWLINE = 1;
    static final int SLASH = 2;
    static final int STAR = 3;
    static final int LPAREN = 4;
    static final int RPAREN = 5;
    static final int PLUS = 6;
    static final int MINUS = 7;
    static final int COLON = 8;
    static final int EQUAL = 9;
    static final int DOT = 10;
    static final int DIGIT = 11;
    static final int LETTER = 12;
    static final int OTHER = 13;

    // ===== Token Types =====
    static final int T_NONE = 0;
    static final int T_DIV = 1;
    static final int T_COMMENT = 2;
    static final int T_NUMBER = 3;
    static final int T_LPAREN = 4;
    static final int T_RPAREN = 5;
    static final int T_PLUS = 6;
    static final int T_MINUS = 7;
    static final int T_TIMES = 8;
    static final int T_ASSIGN = 9;
    static final int T_IDENT = 10;
    static final int T_WHITESPACE = 11;

    // ===== Keyword Table =====
    static final Map<String,Integer> keywordTab = Map.of(
            "read", 100,
            "write", 101
    );

    // ===== Token Table =====
    static final int[] tokenTab = {
            0,           // dummy
            T_NONE,      // 1
            T_DIV,       // 2
            T_NONE,      // 3
            T_NONE,      // 4
            T_NONE,      // 5
            T_LPAREN,    // 6
            T_RPAREN,    // 7
            T_PLUS,      // 8
            T_MINUS,     // 9
            T_TIMES,     // 10
            T_NONE,      // 11
            T_ASSIGN,    // 12
            T_NONE,      // 13
            T_NUMBER,    // 14
            T_NUMBER,    // 15
            T_IDENT,     // 16
            T_WHITESPACE,// 17
            T_COMMENT    // 18
    };

    // ===== DFA Transition Table =====
    static final int[][] nextState = {
            {}, // dummy row 0
            {17,17, 2, 10, 6, 7, 8, 9, 11, -1, 13, 14, 16, -1},     // 1
            {-1, -1, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, // 2
            {3, 18, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},            // 3
            {4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},             // 4
            {4, 4, 18, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},            // 5
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},            // 6
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},            // 7
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},            // 8
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},            // 9
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},            // 10
            {-1,-1,-1,-1,-1,-1,-1,-1,-1, 12,-1,-1,-1,-1},           // 11
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},            // 12
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 15,-1,-1},           // 13
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 15, 14,-1,-1},          // 14
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 15,-1,-1},           // 15
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,16,-1},            // 16
            {17, 17,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},           // 17
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},            // 18
    };

    private final String input;
    private int pos = 0;

    public ScannerCalc(String s) {
        this.input = s;
    }

    // ===== Character Classification =====
    private int classify(char c) {
        if (c == ' ' || c == '\t') return SPACE;
        if (c == '\n' || c == '\r') return NEWLINE;
        if (c == '/') return SLASH;
        if (c == '*') return STAR;
        if (c == '(') return LPAREN;
        if (c == ')') return RPAREN;
        if (c == '+') return PLUS;
        if (c == '-') return MINUS;
        if (c == ':') return COLON;
        if (c == '=') return EQUAL;
        if (c == '.') return DOT;
        if (Character.isDigit(c)) return DIGIT;
        if (Character.isLetter(c)) return LETTER;
        return OTHER;
    }

    private char readChar() {
        if (pos >= input.length()) return '\0';
        return input.charAt(pos++);
    }

    private void unread() {
        if (pos > 0) pos--;
    }

    // ===== Main Scanner =====
    public Token nextToken() {
        int startState = 1;
        int curState = startState;
        int rememberedState = 0;

        StringBuilder image = new StringBuilder();
        StringBuilder rememberedChars = new StringBuilder();

        while (true) {
            char ch = readChar();
            if (ch == '\0') break;
            
            int cls = classify(ch);
            if (cls < 0) break;

            int actionState = nextState[curState][cls];

            if (actionState != -1) {
                // move
                if (tokenTab[curState] != T_NONE) {
                    rememberedState = curState;
                    rememberedChars.setLength(0);
                }
                rememberedChars.append(ch);
                image.append(ch);
                curState = actionState;
                continue;
            }

            // recognize
            if (tokenTab[curState] != T_NONE) {
                unread();
                break;
            }

            // error
            if (rememberedState != 0) {
                unread();
                int diff = rememberedChars.length();
                for (int k = 0; k < diff; k++) image.deleteCharAt(image.length() - 1);
                curState = rememberedState;
                break;
            } else {
                System.out.println("ERROR: illegal input (curState:" + curState +", cls:" + cls + ")");
            }

            // skip and continue
            break;
        }

        int tok = tokenTab[curState];
        
        String img = image.toString();

        if (tok == T_IDENT && keywordTab.containsKey(img))
            tok = keywordTab.get(img);

        return new Token(tok, img);
    }

    // ===== Token Class =====
    public static class Token {
        public final int type;
        public final String image;
        Token(int t, String i) { type = t; image = i; }
        public String toString() { return "Token(" + type + ", \"" + image + "\")"; }
    }

    // ===== Test Main =====
    public static void main(String[] args) throws IOException {
        String input = Files.readString(Path.of("input.txt"));
        ScannerCalc sc = new ScannerCalc(input);
        Token t;
        while ((t = sc.nextToken()).type != T_NONE) {
            if (t.type == T_WHITESPACE) continue;
            System.out.println(t);
        }
    }
}
