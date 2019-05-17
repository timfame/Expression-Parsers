package expression.expressions;

import expression.exceptions.AllExceptions;
import expression.operations.*;

public strictfp class Divide<T> extends AbstractBinaryOperator<T> {

	public Divide(TripleExpression<T> x, TripleExpression<T> y, OperationsType<T> z) {
		super(x, y, z);
	}

	protected T makeOperation(T x, T y) throws AllExceptions {
		return mode.divideOperation(x, y);
	}

}