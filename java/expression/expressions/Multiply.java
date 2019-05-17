package expression.expressions;

import expression.exceptions.AllExceptions;
import expression.operations.OperationsType;

public strictfp class Multiply<T> extends AbstractBinaryOperator<T> {

	public Multiply(TripleExpression<T> x, TripleExpression<T> y, OperationsType<T> z) {
		super(x, y, z);
	}

	protected T makeOperation(T x, T y) throws AllExceptions {
		return mode.multiplyOperation(x, y);
	}

}