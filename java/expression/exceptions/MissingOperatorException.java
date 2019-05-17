package expression.exceptions;

public class MissingOperatorException extends AllExceptions {

	public MissingOperatorException(String str, int index) {
		super(exceptionText(str, index));
	}
}