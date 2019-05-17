package expression.operations;

public class DoubleType implements OperationsType<Double> {

    public Double addOperation(Double x, Double y) {
        return x + y;
    }

    public Double divideOperation(Double x, Double y) {
        return x / y;
    }

    public Double multiplyOperation(Double x, Double y) {
        return x * y;
    }

    public Double negateOperation(Double x) {
        return -x;
    }

    public Double subtractOperation(Double x, Double y) {
        return x - y;
    }

    public Double absOperation(Double x) {
        if (x < 0)
            return negateOperation(x);
        return x;
    }

    public Double sqrOperation(Double x) {
        return x * x;
    }

    public Double modOperation(Double x, Double y){
        return x % y;
    }

    public Double getValueOperation(String myNumber) {
        return Double.parseDouble(myNumber);
    }
}
