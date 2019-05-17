package expression.operations;

import expression.exceptions.*;

public interface OperationsType<T> {
    T addOperation(T x, T y) throws OverflowException;
    T getValueOperation(String str) throws AllExceptions;
    T divideOperation(T x, T y) throws OverflowException, DivisionByZeroException;
    T multiplyOperation(T x, T y) throws OverflowException;
    T negateOperation(T x) throws OverflowException;
    T subtractOperation(T x, T y) throws OverflowException;
    T absOperation(T x) throws OverflowException;
    T sqrOperation(T x) throws OverflowException;
    T modOperation(T x, T y) throws OverflowException, DivisionByZeroException, BigIntegerModulusException;
}
