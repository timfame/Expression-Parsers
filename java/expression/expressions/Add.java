package expression.expressions;

import expression.exceptions.AllExceptions;
import expression.operations.OperationsType;

public class Add<T> extends AbstractBinaryOperator<T> {

	public Add(TripleExpression<T> x, TripleExpression<T> y, OperationsType<T> z) {
		super(x, y, z);
	}

	protected T makeOperation(T x, T y) throws AllExceptions {
		return mode.addOperation(x, y);
	}

}