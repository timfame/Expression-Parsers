package expression.parser;

import expression.expressions.TripleExpression;
import expression.exceptions.*;

public interface Parser {
	TripleExpression parse(String str) throws AllExceptions;
}