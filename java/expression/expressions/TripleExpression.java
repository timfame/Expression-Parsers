package expression.expressions;

import expression.exceptions.*;

public strictfp interface TripleExpression<T> {
	T evaluate(T x, T y, T z) throws AllExceptions;
}