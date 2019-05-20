package md2html;

import java.io.IOException;

public class Md2Html {
    public static void main(String[] args) throws IOException {
        try (FileMdSource files = new FileMdSource(args[0], args[1])) {
            // FileMdSource files = new FileMdSource("input.txt", "output.txt");
            MdConverter res = new MdConverter(files);
            files.write(res.convert());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
