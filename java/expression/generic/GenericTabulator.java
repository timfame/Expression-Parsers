package expression.generic;

import expression.exceptions.AllExceptions;
import expression.exceptions.WrongTypeException;
import expression.expressions.TripleExpression;
import expression.operations.*;
import expression.parser.ExpressionParser;

import java.util.HashMap;

public class GenericTabulator implements Tabulator {

    private static HashMap<String, OperationsType<?>> modes = new HashMap<>();

    static {
        modes.put("i", new IntegerType());
        modes.put("d", new DoubleType());
        modes.put("bi", new BigIntegerType());
        modes.put("u", new IntegerWithoutOverflowType());
        modes.put("f", new FloatType());
        modes.put("b", new ByteType());
    }

    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws AllExceptions {
        if (!modes.containsKey(mode)) {
            throw new WrongTypeException();
        }
        OperationsType<?> currentMode = modes.get(mode);
        return solve(currentMode, expression, x1, x2, y1, y2, z1, z2);
    }

    private <T> Object[][][] solve(OperationsType<T> mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        int dx = x2 - x1 + 1, dy = y2 - y1 + 1, dz = z2 - z1 + 1;
        Object[][][] result = new Object[dx][dy][dz];
        ExpressionParser<T> parser = new ExpressionParser<>(mode);
        TripleExpression<T> ans;
        try {
            ans = parser.parse(expression);
        } catch (AllExceptions e) {
            return result;
        }
        for (int x = 0; x < dx; x++)
            for (int y = 0; y < dy; y++)
                for (int z = 0; z < dz; z++) {
                    try {
                        result[x][y][z] = ans.evaluate(
                                mode.getValueOperation(Integer.toString(x + x1)),
                                mode.getValueOperation(Integer.toString(y + y1)),
                                mode.getValueOperation(Integer.toString(z + z1)));
                    } catch (AllExceptions e) {
                        result[x][y][z] = null;
                    }
                }
        return result;
    }
}
