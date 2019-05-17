package expression.exceptions;

public class BigIntegerModulusException extends AllExceptions {

    public BigIntegerModulusException() {
        super("modulus not positive");
    }
}
