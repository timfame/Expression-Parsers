package expression.exceptions;

public class MissingOperandException extends AllExceptions {

	public MissingOperandException(String str, int index) {
		super(exceptionText(str, index));
	}
}