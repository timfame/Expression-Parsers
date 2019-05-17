package expression.expressions;

public strictfp class Variable<T> implements TripleExpression<T> {
	private final String type;

	public Variable(String str) {
		this.type = str;
	}

	public T evaluate(T x, T y, T z) {
		if (type.equals("x"))
			return x;
		if (type.equals("y"))
			return y;
		return z;
	}
}