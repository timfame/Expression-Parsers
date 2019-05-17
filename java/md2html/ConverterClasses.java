package md2html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConverterClasses {
    private String text;
    private String lastClass;
    private String lastDisplay;
    private int indexOfClass;
    private int currentImage = 0;

    private static ArrayList<String> classes = new ArrayList<>();
    private static ArrayList<String> display = new ArrayList<>();
    private static Map<String,String> specialCharacters = new HashMap<>();
    private Map<String,Boolean> status = new HashMap<>();

    static {
        classes.add("*"); display.add("em");
        classes.add("_"); display.add("em");
        classes.add("**"); display.add("strong");
        classes.add("__"); display.add("strong");
        classes.add("--"); display.add("s");
        classes.add("`"); display.add("code");
        classes.add("++"); display.add("u");
        classes.add("~"); display.add("mark");
        specialCharacters.put("<","&lt;");
        specialCharacters.put(">","&gt;");
        specialCharacters.put("&","&amp;");
    }

    public ConverterClasses(String text) {
        this.text = text;
        status.put("*", false);
        status.put("_", false);
        status.put("**", false);
        status.put("__", false);
        status.put("--", false);
        status.put("`", false);
        status.put("++", false);
        status.put("~", false);
    }

    public void getClass(int index) {
        if (currentImage > 0) {
            String str = stepImage(index);
            if (str == null) {
                lastClass = lastDisplay = text.substring(index, index + 1);
            }
            return;
        }
        String res = "";
        for (int i = 0; i < classes.size(); i++) {
            String currentClass = classes.get(i);
            if (index + currentClass.length() - 1 < text.length() &&
                text.substring(index, index + currentClass.length()).equals(currentClass)) {
                res = currentClass;
            }
        }
        indexOfClass = -1;
        if (res.equals("*") || res.equals("_")) {
            boolean leftSpace = true, rightSpace = true;
            if (index - 1 >= 0 && !Character.isWhitespace(text.charAt(index - 1))) {
                leftSpace = false;
            }
            if (index + 1 < text.length() && !Character.isWhitespace(text.charAt(index + 1))) {
                rightSpace = false;
            }
            if (leftSpace && rightSpace) {
                lastClass = res;
                lastDisplay = res;
                return;
            }
        }
        if (res != null && !res.equals("")) {
            lastClass = res;
            indexOfClass = classes.indexOf(lastClass);
            Boolean flag = status.get(lastClass);
            if (flag != null) {
                if (flag == true) {
                    flag = false;
                } else {
                    flag = true;
                }
            }
            status.put(lastClass, flag);
            return;
        }
        if (text.charAt(index) == '\\' && index + 1 < text.length() &&
                (text.charAt(index + 1) == '*' || text.charAt(index + 1) == '_')) {
            lastClass = text.substring(index, index + 2);
            lastDisplay = text.substring(index + 1, index + 2);
            return;
        }
        if (specialCharacters.containsKey(text.substring(index, index + 1))) {
            lastClass = text.substring(index, index + 1);
            lastDisplay = specialCharacters.get(text.substring(index, index + 1));
            return;
        }
        String str = stepImage(index);
        if (str == null) {
            lastClass = lastDisplay = text.substring(index, index + 1);
        }
        return;
    }

    public String getDisplay(int index) {
        if (lastClass.equals("[")) {
            lastDisplay = makeLink(index).toString();
        }
        if (indexOfClass >= 0) {
            StringBuilder openOrClose = new StringBuilder();
            Boolean flag = status.get(lastClass);
            if (flag != null && !flag) {
                openOrClose.append("</");
            } else {
                openOrClose.append("<");
            }
            return (openOrClose.append(display.get(classes.indexOf(lastClass))).append(">")).toString();
        }
        return lastDisplay;
    }

    public int getShift() {
        return lastClass.length();
    }

    private String stepImage(int index) {
        if (index + 1 < text.length() && text.substring(index, index + 2).equals("![")) {
            currentImage = 1;
            lastClass = text.substring(index, index + 2);
            lastDisplay = "<img alt='";
            return lastClass;
        }
        if (index + 1 < text.length() && text.substring(index, index + 2).equals("](") && currentImage == 1) {
            currentImage = 2;
            lastClass = text.substring(index, index + 2);
            lastDisplay = "' src='";
            return lastClass;
        }
        if (text.charAt(index) == ')' && currentImage == 2) {
            currentImage = 0;
            lastClass = text.substring(index, index + 1);
            lastDisplay = "'>";
            return lastClass;
        }
        return null;
    }

    private StringBuilder makeLink(int index) {
        StringBuilder res = new StringBuilder();
        StringBuilder textLink = new StringBuilder();
        StringBuilder hrefLink = new StringBuilder();
        boolean itWasLink = false;
        int stepLink = 0;
        int i = index + 1;
        while (i < text.length()) {
            if (i + 1 < text.length() && text.substring(i, i + 2).equals("](") && stepLink == 0) {
                i += 2;
                stepLink = 1;
            }
            if (text.charAt(i) == ')' && stepLink == 1) {
                itWasLink = true;
                break;
            }
            if (stepLink == 0) {
                getClass(i);
                String replace = getDisplay(i);
                i += getShift();
                textLink.append(replace);
            }
            if (stepLink == 1) {
                hrefLink.append(text.charAt(i));
                i++;
            }
        }
        indexOfClass = -1;
        if (itWasLink) {
            lastClass = text.substring(index, i + 1);
            return res.append("<a href='").append(hrefLink).append("'>").append(textLink).append("</a>");
        }
        i = index;
        while (i < text.length()) {
            res.append(text.charAt(i));
            i++;
        }
        lastClass = res.toString();
        return res;
    }
}
