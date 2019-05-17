package expression.expressions;

import expression.exceptions.*;
import expression.operations.OperationsType;

public strictfp abstract class AbstractBinaryOperator<T> implements TripleExpression<T> {
	private final TripleExpression<T> op1;
	private final TripleExpression<T> op2;
	protected final OperationsType<T> mode;

	AbstractBinaryOperator(TripleExpression<T> a, TripleExpression<T> b, OperationsType<T> c) {
		this.op1 = a;
		this.op2 = b;
		this.mode = c;
	}

	protected abstract T makeOperation(T x, T y) throws AllExceptions;

	public T evaluate(T x, T y, T z) throws AllExceptions{
		return makeOperation(op1.evaluate(x, y, z), op2.evaluate(x, y, z));
	}
}