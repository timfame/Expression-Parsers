package expression.expressions;

import expression.exceptions.AllExceptions;
import expression.operations.*;

public strictfp class Subtract<T> extends AbstractBinaryOperator<T> {

	public Subtract(TripleExpression<T> x, TripleExpression<T> y, OperationsType<T> z) {
		super(x, y, z);
	}

	protected T makeOperation(T x, T y) throws AllExceptions {
		return mode.subtractOperation(x, y);
	}

}