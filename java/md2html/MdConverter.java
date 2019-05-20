package md2html;

import java.io.IOException;

public class MdConverter {
    private FileMdSource files;
    private String currentParagraph;

    public MdConverter(FileMdSource files) {
        this.files = files;
    }

    public String convert() throws IOException {
        currentParagraph = files.nextParagraph();
        StringBuilder res = new StringBuilder();
        while (!files.endOfFile()) {
            int cntHeader = 0;
            while (cntHeader < currentParagraph.length() && currentParagraph.charAt(cntHeader) == '#' && cntHeader < 6) {
                cntHeader++;
            }
            String type = "h";
            if (cntHeader > 0 && cntHeader < currentParagraph.length() &&
                    !Character.isWhitespace(currentParagraph.charAt(cntHeader))) {
                cntHeader = 0;
            }
            if (cntHeader == 0) {
                type = "p";
            } else {
                type = type + cntHeader;
                cntHeader++;
            }
            res.append("<" + type + ">").append(convertParagraph(cntHeader)).append("</" + type + ">").append("\r\n");
            currentParagraph = files.nextParagraph();
        }
        return res.toString();
    }

    private StringBuilder convertParagraph(int start) {
        ConverterClasses classes = new ConverterClasses(currentParagraph);
        StringBuilder res = new StringBuilder();
        int i = start;
        while (i < currentParagraph.length()) {
            classes.getClass(i);
            String replace = classes.getDisplay(i);
            i += classes.getShift();
            res.append(replace);
        }
        return res;
    }
}
