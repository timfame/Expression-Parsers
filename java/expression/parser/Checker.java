package expression.parser;

import expression.exceptions.*;

import java.util.Map;

class Checker {
    private String str;

    Checker(String s) {
        this.str = s;
    }

    void check(ExpressionParser.Type oldType, ExpressionParser.Type newType, int index) throws AllExceptions {
        if (newType == ExpressionParser.Type.CLOSE_BRACKET && oldType == ExpressionParser.Type.OPEN_BRACKET) {
            throw new EmptyBracketsException(str, index);
        }
        for (Map.Entry<String, ExpressionParser.Type> entry : ExpressionParser.operator.entrySet()) {
            String key = entry.getKey();
            if (index + key.length() < str.length() && str.substring(index, index + key.length()).equals(key)) {
                ExpressionParser.Type value = entry.getValue();
                if (newType == value && operatorCharacter(index + key.length())) {
                    throw new OperatorNotFoundException(str, index);
                }
            }
        }
        switch (newType) {
            case ADD:
            case MULTIPLY:
            case DIVIDE:
            case CLOSE_BRACKET:
            case MOD:
                if (oldType != ExpressionParser.Type.CONST && oldType != ExpressionParser.Type.VARIABLE && oldType != ExpressionParser.Type.CLOSE_BRACKET) {
                    throw new MissingOperandException(str,  index);
                }
                break;
            case OPEN_BRACKET:
            case VARIABLE:
            case CONST:
            case ABS:
            case SQR:
                if (oldType == ExpressionParser.Type.CONST || oldType == ExpressionParser.Type.VARIABLE || oldType == ExpressionParser.Type.CLOSE_BRACKET) {
                    throw new MissingOperatorException(str, index);
                }
                break;
        }
    }
    private boolean operatorCharacter(int index) {
        return Character.isLetterOrDigit(str.charAt(index));
    }

}
