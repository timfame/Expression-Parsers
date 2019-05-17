package expression.exceptions;

public class EmptyBracketsException extends AllExceptions {

	public EmptyBracketsException(String str, int index) {
		super(exceptionText(str, index));
	}
}