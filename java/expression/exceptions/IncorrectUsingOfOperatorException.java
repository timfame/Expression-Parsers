package expression.exceptions;

public class IncorrectUsingOfOperatorException extends AllExceptions {

	public IncorrectUsingOfOperatorException(String str, int index) {
		super(exceptionText(str, index));
	}
}