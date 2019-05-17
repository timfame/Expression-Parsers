package expression.expressions;

import expression.exceptions.AllExceptions;
import expression.exceptions.OverflowException;
import expression.operations.*;

public strictfp class Abs<T> extends AbstractUnaryOperator<T> {

    public Abs(TripleExpression<T> x, OperationsType<T> y) {
        super(x, y);
    }

    protected T makeOperation(T x) throws AllExceptions {
        return mode.absOperation(x);
    }

}