package expression.operations;

import expression.exceptions.DivisionByZeroException;

public class IntegerWithoutOverflowType implements OperationsType<Integer> {

    public Integer addOperation(Integer x, Integer y) {
        return x + y;
    }

    public Integer divideOperation(Integer x, Integer y) throws DivisionByZeroException {
        if (y == 0) {
            throw new DivisionByZeroException();
        }
        return x / y;
    }

    public Integer multiplyOperation(Integer x, Integer y) {
        return x * y;
    }

    public Integer negateOperation(Integer x) {
        return -x;
    }

    public Integer subtractOperation(Integer x, Integer y) {
        return x - y;
    }

    public Integer absOperation(Integer x) {
        if (x < 0)
            return negateOperation(x);
        return x;
    }

    public Integer sqrOperation(Integer x) {
        return x * x;
    }

    public Integer modOperation(Integer x, Integer y) throws DivisionByZeroException {
        if (y == 0) {
            throw new DivisionByZeroException();
        }
        return x % y;
    }

    public Integer getValueOperation(String myNumber) {
        return Integer.parseInt(myNumber);
    }

}
