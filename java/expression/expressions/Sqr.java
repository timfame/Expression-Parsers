package expression.expressions;

import expression.exceptions.AllExceptions;
import expression.operations.*;

public strictfp class Sqr<T> extends AbstractUnaryOperator<T> {

    public Sqr(TripleExpression<T> x, OperationsType<T> y) {
        super(x, y);
    }

    protected T makeOperation(T x) throws AllExceptions {
        return mode.sqrOperation(x);
    }

}