package expression.exceptions;

public class InvalidBracketsException extends AllExceptions {
	
	public InvalidBracketsException(String str, int index) {
		super(exceptionText(str, index));
	}
}