package expression.operations;

import expression.exceptions.*;

public class IntegerType implements OperationsType<Integer> {

    public Integer addOperation(Integer x, Integer y) throws OverflowException{
        if (x > 0 && y > Integer.MAX_VALUE - x) {
            throw new OverflowException("add", 0);
        }
        if (x < 0 && y < Integer.MIN_VALUE - x) {
            throw new OverflowException("add", 0);
        }
        return x + y;
    }

    public Integer divideOperation(Integer x, Integer y) throws OverflowException, DivisionByZeroException{
        if (y == 0) {
            throw new DivisionByZeroException();
        }
        if (y == -1 && x == Integer.MIN_VALUE) {
            throw new OverflowException("divide", 0);
        }
        return x / y;
    }

    public Integer multiplyOperation(Integer x, Integer y) throws OverflowException{
        if (x > 0 && y > 0 && Integer.MAX_VALUE / y < x) {
            throw new OverflowException("multiply", 0);
        }
        if (x < 0 && y < 0 && Integer.MAX_VALUE / y > x) {
            throw new OverflowException("multiply", 0);
        }
        if (x > 0 && y < 0 && Integer.MIN_VALUE / x > y) {
            throw new OverflowException("multiply", 0);
        }
        if (x < 0 && y > 0 && Integer.MIN_VALUE / y > x) {
            throw new OverflowException("multiply", 0);
        }
        return x * y;
    }

    public Integer negateOperation(Integer x) throws OverflowException{
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException("negate", 0);
        }
        return (-x);
    }

    public Integer subtractOperation(Integer x, Integer y) throws OverflowException{
        if (y > 0 && Integer.MIN_VALUE + y > x) {
            throw new OverflowException("subtract", 0);
        }
        if (y < 0 && Integer.MAX_VALUE + y < x) {
            throw new OverflowException("subtract", 0);
        }
        return x - y;
    }

    public Integer absOperation(Integer x) throws OverflowException {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException("abs", 0);
        }
        if (x < 0)
            return negateOperation(x);
        return x;
    }

    public Integer sqrOperation(Integer x) throws OverflowException {
        if (x > 0 && Integer.MAX_VALUE / x < x) {
            throw new OverflowException("sqr", 0);
        }
        if (x < 0 && Integer.MAX_VALUE / x > x) {
            throw new OverflowException("sqr", 0);
        }
        return x * x;
    }

    public Integer modOperation(Integer x, Integer y) throws DivisionByZeroException {
        if (y == 0) {
            throw new DivisionByZeroException();
        }
        return x % y;
    }

    public Integer getValueOperation(String myNumber) throws OverflowException{
        Integer res;
        try {
            res = Integer.parseInt(myNumber);
        } catch (NumberFormatException e) {
            throw new OverflowException(myNumber, 0);
        }
        return res;
    }

}
