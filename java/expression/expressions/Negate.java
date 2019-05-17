package expression.expressions;

import expression.exceptions.AllExceptions;
import expression.operations.*;

public strictfp class Negate<T> extends AbstractUnaryOperator<T> {
	
	public Negate(TripleExpression<T> x, OperationsType<T> y) {
		super(x, y);
	}	

	protected T makeOperation(T x) throws AllExceptions {
		return mode.negateOperation(x);
	}

}