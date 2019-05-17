package expression.operations;

import expression.exceptions.DivisionByZeroException;

public class ByteType implements OperationsType<Byte> {

    public Byte addOperation(Byte x, Byte y) {
        return (byte)(x + y);
    }

    public Byte divideOperation(Byte x, Byte y) throws DivisionByZeroException {
        if (y == 0) {
            throw new DivisionByZeroException();
        }
        return (byte)(x / y);
    }

    public Byte multiplyOperation(Byte x, Byte y) {
        return (byte)(x * y);
    }

    public Byte negateOperation(Byte x) {
        return (byte)(-x);
    }

    public Byte subtractOperation(Byte x, Byte y) {
        return (byte)(x - y);
    }

    public Byte absOperation(Byte x) {
        if (x < 0)
            return negateOperation(x);
        return x;
    }

    public Byte sqrOperation(Byte x) {
        return (byte)(x * x);
    }

    public Byte modOperation(Byte x, Byte y) throws DivisionByZeroException {
        if (y == 0) {
            throw new DivisionByZeroException();
        }
        return (byte)(x % y);
    }

    public Byte getValueOperation(String myNumber) {
        return (byte)(Integer.parseInt(myNumber));
    }

}
