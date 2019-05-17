package expression.expressions;

import expression.exceptions.AllExceptions;
import expression.operations.OperationsType;

public class Mod<T> extends AbstractBinaryOperator<T> {

    public Mod(TripleExpression<T> x, TripleExpression<T> y, OperationsType<T> z) {
        super(x, y, z);
    }

    protected T makeOperation(T x, T y) throws AllExceptions {
        return mode.modOperation(x, y);
    }

}