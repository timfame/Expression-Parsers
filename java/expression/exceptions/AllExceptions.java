package expression.exceptions;

public class AllExceptions extends Exception {
	
	public AllExceptions(String str) {
		super(str);
	}

	static protected String exceptionText(String str, int index) {
		int bad = 0;
		int left = index - 1;
		while (left >= 0 && index - left < 9 && bad == 0) {
			if (!good(str.charAt(left)) ) {
				bad++;
			}
			left--;
		}
		int right = index + 1;
		if (right > str.length()) {
			right = str.length();
		}
		bad = 0;
		while (right < str.length() && right - index < 9 && bad == 0) {
			if (!good(str.charAt(right))) {
				bad++;
			}
			right++;
		}
		return "->" + str.substring(left + 1, right) + "<-";
	}

	static private boolean good(char c) {
		return Character.isLetterOrDigit(c) || Character.isWhitespace(c) ||
                c == '-' || c == '+' || c == '/' || c == '*' || c == '.';
	}
}