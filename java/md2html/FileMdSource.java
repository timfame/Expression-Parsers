package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileMdSource extends MdSource implements AutoCloseable{
    private Reader reader;
    private Writer writer;

    public FileMdSource(final String input, final String output) throws IOException{
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8));
    }

    protected char readChar() throws IOException {
        final int cur = reader.read();
        return (cur == -1 ? END : (char)cur);
    }

    public void write(final String str) throws IOException{
        writer.write(str);
    }

    @Override
    public void close() {
        close(reader, writer);
    }

    private void close(AutoCloseable... args) {
        for (AutoCloseable arg : args)
            if (arg != null) {
                try {
                    arg.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
    }
}
