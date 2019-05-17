package expression.exceptions;

public class OperatorNotFoundException extends AllExceptions {

	public OperatorNotFoundException(String str, int index) {
		super(exceptionText(str, index));
	}
}