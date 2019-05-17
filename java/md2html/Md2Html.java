package md2html;

import java.io.IOException;

public class Md2Html {
    public static void main(String[] args) throws IOException {
        try (FileMdSourse files = new FileMdSourse(args[0], args[1])) {
            // FileMdSourse files = new FileMdSourse("input.txt", "output.txt");
            MdConverter res = new MdConverter(files);
            files.write(res.convert());
            files.close();
        } catch
    }
}
