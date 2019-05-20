package md2html;

import java.io.IOException;

public abstract class MdSource {
    public static char END = '\0';

    protected int pos;
    private char c;
    private String p;

    protected abstract char readChar() throws IOException;

    public char getChar() {
        return c;
    }

    public char nextChar() throws IOException{
        c = readChar();
        if (c == '\r')
            c = readChar();
        pos++;
        return c;
    }

    public String getParagraph() {
        return p;
    }

    public String nextParagraph() throws IOException {
        StringBuilder res = new StringBuilder();
        int enter = 0;
        nextChar();
        skipEnters();
        while (getChar() != MdSource.END) {
            if (getChar() == '\n') {
                enter++;
            } else {
                if (enter == 1) {
                    res.append("\n");
                }
                enter = 0;
            }
            if (enter == 2) {
                break;
            }
            if (enter == 0) {
                res.append(getChar());
            }
            nextChar();
        }
        return (p = res.toString());
    }

    private void skipEnters() throws IOException{
        while (getChar() == '\n') {
            nextChar();
        }
    }

    public boolean endOfFile() {
        return (p == null || p.equals(""));
    }
}
