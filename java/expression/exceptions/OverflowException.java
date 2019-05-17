package expression.exceptions;

public class OverflowException extends AllExceptions {
	
	public OverflowException(String str, int index) {
		super(exceptionText(str, index));
	}
}