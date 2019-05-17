package expression.operations;

public class FloatType implements OperationsType<Float> {

    public Float addOperation(Float x, Float y) {
        return x + y;
    }

    public Float divideOperation(Float x, Float y) {
        return x / y;
    }

    public Float multiplyOperation(Float x, Float y) {
        return x * y;
    }

    public Float negateOperation(Float x) {
        return -x;
    }

    public Float subtractOperation(Float x, Float y) {
        return x - y;
    }

    public Float absOperation(Float x) {
        if (x < 0)
            return negateOperation(x);
        return x;
    }

    public Float sqrOperation(Float x) {
        return x * x;
    }

    public Float modOperation(Float x, Float y) {
        return x % y;
    }

    public Float getValueOperation(String myNumber) {
        return Float.parseFloat(myNumber);
    }
}