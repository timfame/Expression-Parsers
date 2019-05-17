let countArgs = [];
let operations = [];
let constants = [];

let addOperation = function(str, type, cnt) {
    countArgs[str] = cnt;
    operations[str] = type;
};

let AnyOperation = (type) => (... op) => (... args) => type(... op.map((x) => x(... args)));

let cnst = (x) => () => (x);
let variable = (s) => (... args) => args[s === "x" ? 0 : (s === "y" ? 1 : 2)];
let one = AnyOperation(() => 1)();
let two = AnyOperation(() => 2)();
let add = AnyOperation((a, b) => a + b);
let subtract = AnyOperation((a, b) => a - b);
let multiply = AnyOperation((a, b) => a * b);
let divide = AnyOperation((a, b) => a / b);
let negate = AnyOperation((a) => -a);
let abs = AnyOperation((a) => Math.abs(a));
let iff = AnyOperation( (a, b, c) => a >= 0 ? b : c);

addOperation("+", add, 2);
addOperation("-", subtract, 2);
addOperation("*", multiply, 2);
addOperation("/", divide, 2);
addOperation("negate", negate, 1);
addOperation("abs", abs, 1);
addOperation("iff", iff, 3);
addOperation("x", variable, 0);
addOperation("y", variable, 0);
addOperation("z", variable, 0);
constants["one"] = () => 1;
constants["two"] = () => 2;

let parse = (exp) => exp.trim().split(/\s+/).reduce(func, []).pop();

let func = (stack, token) => {
    if (isNumber(token)) {
        stack.push(cnst(parseInt(token, 10)));
    } else if (constants[token] === undefined) {
        stack.push(operations[token](... countArgs[token] === 0 ? [token] : stack.splice(-countArgs[token])));
    } else {
        stack.push(constants[token]);
    }
    return stack;
};

function isNumber(str) {
    return !isNaN(parseInt(str, 10));
}
