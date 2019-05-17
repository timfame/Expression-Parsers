package expression.expressions;

import expression.exceptions.*;
import expression.operations.OperationsType;

public strictfp abstract class AbstractUnaryOperator<T> implements TripleExpression<T> {
	private final TripleExpression<T> op;
	protected final OperationsType<T> mode;

	AbstractUnaryOperator(TripleExpression<T> a, OperationsType<T> b) {
		this.op = a;
		this.mode = b;
	}

	protected abstract T makeOperation(T x) throws AllExceptions;

	public T evaluate(T x, T y, T z) throws AllExceptions{
		return makeOperation(op.evaluate(x, y, z));
	}
}