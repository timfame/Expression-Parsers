package expression.operations;

import expression.exceptions.BigIntegerModulusException;
import expression.exceptions.DivisionByZeroException;

import java.math.BigInteger;

public class BigIntegerType implements OperationsType<BigInteger> {

    public BigInteger addOperation(BigInteger x, BigInteger y) {
        return x.add(y);
    }

    public BigInteger divideOperation(BigInteger x, BigInteger y) throws DivisionByZeroException {
        if (y.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException();
        }
        return x.divide(y);
    }

    public BigInteger multiplyOperation(BigInteger x, BigInteger y) {
        return x.multiply(y);
    }

    public BigInteger negateOperation(BigInteger x) {
        return x.negate();
    }

    public BigInteger subtractOperation(BigInteger x, BigInteger y) {
        return x.subtract(y);
    }

    public BigInteger absOperation(BigInteger x) {
        if (x.compareTo(BigInteger.ZERO) < 0)
            return negateOperation(x);
        return x;
    }

    public BigInteger sqrOperation(BigInteger x) {
        return x.multiply(x);
    }

    public BigInteger modOperation(BigInteger x, BigInteger y) throws DivisionByZeroException, BigIntegerModulusException {
        if (y.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException();
        }
        if (y.compareTo(BigInteger.ZERO) < 0) {
            throw new BigIntegerModulusException();
        }
        return x.mod(y);
    }

    public BigInteger getValueOperation(String myNumber) {
        return new BigInteger(myNumber);
    }

}

