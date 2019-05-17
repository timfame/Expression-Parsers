package expression.parser;

import expression.exceptions.*;
import expression.expressions.*;
import expression.operations.DoubleType;
import expression.operations.OperationsType;

import java.util.HashMap;
import java.util.Map;

public class ExpressionParser<T> implements Parser {
	enum Type {ABS, SQR, MOD, ADD, SUBTRACT, MULTIPLY, DIVIDE, CONST, VARIABLE, OPEN_BRACKET, CLOSE_BRACKET, START, END}
	static HashMap<String, Type> operator = new HashMap<String, Type>();

	static {
		operator.put("abs", Type.ABS);
		operator.put("square", Type.SQR);
		operator.put("mod", Type.MOD);
	}

	private String str;
	private Type currentType;
	private int currentIndex;
	private T currentConst;
	private int currentBalance;
	private String currentVariable;
	private Checker checker;
	private OperationsType<T> mode;

	public ExpressionParser(OperationsType<T> m) {
		this.mode = m;
	}

	public TripleExpression<T> parse(String s) throws AllExceptions{
		checker = new Checker(s);
		str = s;
		currentIndex = 0;
		currentBalance = 0;
		currentType = Type.START;
		return parseAddSubtract();
	}

	private TripleExpression<T> parseAddSubtract() throws AllExceptions{
		TripleExpression<T> currentResult = parseMultiplyDivide();
		if (currentResult == null) {
			currentResult = new Const<>(null);
		}
		while (true) {
			switch (currentType) {
				case ADD:
					currentResult = new Add<>(currentResult, parseMultiplyDivide(), mode);
					break;
				case SUBTRACT:
					currentResult = new Subtract<>(currentResult, parseMultiplyDivide(), mode);
					break;
				default:
					return currentResult;
			}
		}
	}

	private TripleExpression<T> parseMultiplyDivide() throws AllExceptions{
		TripleExpression<T> currentResult = parseUnaryOperations();
		while (true) {
			switch (currentType) {
				case MOD:
					currentResult = new Mod<>(currentResult, parseUnaryOperations(), mode);
					break;
				case MULTIPLY:
					currentResult = new Multiply<>(currentResult, parseUnaryOperations(), mode);
					break;
				case DIVIDE:
					currentResult = new Divide<>(currentResult, parseUnaryOperations(), mode);
					break;
				default:
					return currentResult;
			}
		}
	}

	private TripleExpression<T> parseUnaryOperations() throws AllExceptions{
		TripleExpression<T> currentResult = null;
		next();
		switch (currentType) {
			case ABS:
				currentResult = new Abs<>(parseUnaryOperations(), mode);
				break;
			case SQR:
				currentResult = new Sqr<>(parseUnaryOperations(), mode);
				break;
			case SUBTRACT:
				currentResult = new Negate<>(parseUnaryOperations(), mode);
				break;
			case CONST:
				currentResult = new Const<>(currentConst);
				next();
				break;
			case VARIABLE:
				currentResult = new Variable<>(currentVariable);
				next();
				break;
			case OPEN_BRACKET:
				currentResult = parseAddSubtract();
				next();
				break;
		}
		return currentResult;
	}

	private void nextNotWhiteSpace() {
		while (currentIndex < str.length() && Character.isWhitespace(str.charAt(currentIndex))) {
			currentIndex++;
		}
	}

	private boolean numberCharacter(char c) {
		return Character.isDigit(c) || c == '.';
	}

	private String getValue() throws MissingOperandException{
		int start = currentIndex;
		if (str.charAt(currentIndex) == '-') {
			currentIndex++;
		}
		if (currentIndex == str.length()) {
			throw new MissingOperandException(str, currentIndex);
		}
		int badCharacter = 0;
		while (currentIndex < str.length() && numberCharacter(str.charAt(currentIndex))) {
			if (str.charAt(currentIndex) == '.') {
				badCharacter++;
			}
			if (badCharacter > ((mode.getClass() == DoubleType.class) ? 1 : 0)) {
				break;
			}
			currentIndex++;
		}
		return str.substring(start, currentIndex);
	}

	private void next() throws AllExceptions{
		nextNotWhiteSpace();
		if (currentIndex >= str.length()) {
			if (currentType != Type.CONST && currentType != Type.VARIABLE && currentType != Type.CLOSE_BRACKET) {
				throw new MissingOperandException(str, currentIndex);
			}
			currentType = Type.END;
			return;
		}
		Type previousType = currentType;
		int previousIndex = currentIndex;
		switch (str.charAt(currentIndex)) {
			case '+':
				currentType = Type.ADD;
				break;
			case '-':
				if (currentType == Type.CONST || currentType == Type.VARIABLE || currentType == Type.CLOSE_BRACKET) {
					currentType = Type.SUBTRACT;
				} else if (currentIndex + 1 < str.length() && Character.isDigit(str.charAt(currentIndex + 1))) {
					String myNumber = getValue();
					currentConst = mode.getValueOperation(myNumber);
					currentType = Type.CONST;
				} else {
					currentType = Type.SUBTRACT;
				}
				break;
			case '*':
				currentType = Type.MULTIPLY;
				break;
			case '/':
				currentType = Type.DIVIDE;
				break;
			case '(':
				currentType = Type.OPEN_BRACKET;
				currentBalance++;
				if (currentBalance > str.length() - currentIndex - 1) {
					throw new InvalidBracketsException(str, currentIndex);
				}
				break;
			case ')':
				currentType = Type.CLOSE_BRACKET;
				currentBalance--;
				if (currentBalance < 0) {
					throw new InvalidBracketsException(str, currentIndex);
				}
				break;
			case 'x':
			case 'y':
			case 'z':
				currentVariable = str.substring(currentIndex, currentIndex + 1);
				currentType = Type.VARIABLE;
				break;
			default:
				if (Character.isDigit(str.charAt(currentIndex))) {
					String myNumber = getValue();
					currentConst = mode.getValueOperation(myNumber);
					currentType = Type.CONST;
				} else {
					boolean foundOperator = false;
					for (Map.Entry<String, Type> entry : operator.entrySet()) {
						String key = entry.getKey();
						if (currentIndex + key.length() - 1 < str.length() && str.substring(currentIndex, currentIndex + key.length()).equals(key)) {
							currentIndex += key.length() - 1;
							currentType = entry.getValue();
							foundOperator = true;
							break;
						}
					}
					if (!foundOperator) {
						throw new OperatorNotFoundException(str, currentIndex);
					}
				}
				break;
		}
		checker.check(previousType, currentType, previousIndex);
		if (currentType != Type.CONST) {
			currentIndex++;
		}
	}
}