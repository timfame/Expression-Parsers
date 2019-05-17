package md2html;

import java.io.*;

public class FileMdSourse extends MdSourse {
    private Reader reader = null;
    private Writer writer = null;

    public FileMdSourse(final String input, final String output) throws IOException{
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF-8"));
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
    }

    protected char readChar() throws IOException {
        final int cur = reader.read();
        return (cur == -1 ? END : (char)cur);
    }

    public void write(final String str) throws IOException{
        writer.write(str);
    }

    public void close() throws IOException{
        reader.close();
        writer.close();
    }
}
