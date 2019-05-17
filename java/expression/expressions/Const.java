package expression.expressions;

public strictfp class Const<T> implements TripleExpression<T> {
	private final T curNumber;

	public Const(T x) {
		curNumber = x;
	}

	public T evaluate(T x, T y, T z) {
		return curNumber;
	}
}