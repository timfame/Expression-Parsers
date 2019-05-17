"use strict";

if (!Array.prototype.last) {
    Array.prototype.last = function() { return this[this.length - 1]};
}
if (!Array.prototype.empty) {
    Array.prototype.empty = function() { return this.length === 0};
}

const operators = (function(){

    function AbstractOperator(... op) {
        this.op = op;
    }
    AbstractOperator.prototype.evaluate = function(... args) {
       return this.makeOperation(...this.op.map(function(operator) { return operator.evaluate(... args) }));
    };
    AbstractOperator.prototype.toString = function() {
        return this.op.map((x) => x.toString()).join(" ") + " " + this.operatorCharacter();
    };
    AbstractOperator.prototype.prefix = function() {
        return "(" + this.operatorCharacter() + " " + this.op.map((x) => x.prefix()).join(" ") + ")";
    };
    AbstractOperator.prototype.postfix = function() {
        return "(" + this.op.map((x) => x.postfix()).join(" ") + " " + this.operatorCharacter() + ")";
    };
    AbstractOperator.prototype.diff = function(x) {
        return this.typeDiff(... [... this.op, ... this.op.map((cur) => cur.diff(x))]);
    };
    AbstractOperator.prototype.simplify = function() {
        let simple = this.op.map( (x) => x.simplify());
        let result = this.typeSimplify(... simple);
        if (simple.every((cur) => (cur instanceof Const))) {
            return new Const(result.evaluate());
        }
        return result;
    };

    let ConstOrVariable = function() {
        let operator = function (a) {
            this.getValue = function() { return a};
        };
        operator.prototype = Object.create(AbstractOperator.prototype);
        operator.prototype.toString = function() {return this.getValue().toString()};
        operator.prototype.prefix = function() {return this.getValue().toString()};
        operator.prototype.postfix = function() {return this.getValue().toString()};
        operator.prototype.simplify = function() { return this};
        return operator;
    };
    const Const = ConstOrVariable();
    Const.prototype.evaluate = function() { return this.getValue()};
    Const.prototype.diff = function() { return Const.ZERO};
    Const.HALF = new Const(0.5);
    Const.ZERO = new Const(0);
    Const.ONE = new Const(1);
    Const.TWO = new Const(2);

    function isZero(f) {
        return f instanceof Const && f.getValue() === 0;
    }

    function isOne(f) {
        return f instanceof Const && f.getValue() === 1;
    }

    const Variable = ConstOrVariable();
    Variable.prototype.evaluate = function (... args) {
        return args[this.getValue() === "x" ? 0 : this.getValue() === "y" ? 1 : 2]
    };
    Variable.prototype.diff = function(x) { return (this.getValue() === x ? Const.ONE : Const.ZERO)};

    function isEqualVariables(f, g) {
        return (f instanceof Variable) && (g instanceof Variable) && (f.getValue() === g.getValue());
    }

    let newOperator = function(func, character, diffFunction, simplifyFunction) {
        let operator = function (... args) {
            AbstractOperator.apply(this, args);
        };
        operator.prototype = Object.create(AbstractOperator.prototype);
        operator.prototype.makeOperation = func;
        operator.prototype.operatorCharacter = () => character;
        operator.prototype.typeDiff = diffFunction;
        operator.prototype.typeSimplify = simplifyFunction;
        return operator;
    };

    const Add = newOperator(
        (a, b) => a + b, "+",
        (f, g, df, dg) => new Add(df, dg),
        (f, g) => isZero(f) ? g : isZero(g) ? f : new Add(f, g)
    );

    const Subtract = newOperator(
        (a, b) => a - b, "-",
        (f, g, df, dg) => new Subtract(df, dg),
        (f, g) => isZero(g) ? f : isEqualVariables(f, g) ? Const.ZERO : new Subtract(f, g)
    );

    const Multiply = newOperator(
        (a, b) => a * b, "*",
        (f, g, df, dg) => new Add(new Multiply(df, g), new Multiply(f, dg)),
        (f, g) => (isZero(f) || isZero(g)) ? Const.ZERO :
            isOne(f) ? g : isOne(g) ? f : new Multiply(f, g)
    );

    const Divide = newOperator(
        (a, b) => a / b, "/",
        (f, g, df, dg) => new Divide(new Subtract(new Multiply(df, g), new Multiply(f, dg)),
                                                new Multiply(g, g)),
        (f, g) => isZero(f) ? Const.ZERO : isOne(g) ? f :
            isEqualVariables(f, g) ? Const.ONE : new Divide(f, g)
    );

    const Negate = newOperator(
        (a) => -a, "negate",
        (f, df) => new Negate(df),
        (f) => new Negate(f)
    );

    const ArcTan = newOperator(
        (a) => Math.atan(a), "atan",
        (f, df) => (new Divide(df, new Add(Const.ONE, new Multiply(f, f)))),
        (f) => new ArcTan(f)
    );

    const ArcTan2 = newOperator(
        (a, b) => Math.atan2(a, b), "atan2",
        (f, g, df, dg) => new Divide(new Subtract(new Multiply(df, g), new Multiply(f, dg)),
                                                new Add(new Multiply(f, f), new Multiply(g, g))),
        (f, g) => new ArcTan2(f, g)
    );

    const Sum = newOperator(
        (... args) => args.reduce((curSum, x) => curSum += x, 0), "sum",
        (... f) => new Sum(... f.slice(f.length / 2))
    );

    const Avg = newOperator(
        (... args) => args.reduce((curSum, x) => curSum += x, 0) / args.length, "avg",
        (... f) => new Divide( new Sum(... f.slice(f.length / 2)), new Const(f.length / 2))
    );

    const Exp = newOperator(
        (a) => Math.exp(a), "exp",
        (f, df) => new Multiply(new Exp(f), df)
    );

    const Sumsq = newOperator(
        (... args) => args.reduce((curSum, x) => curSum += x * x, 0), "sumsq",
        (... f) => new Sum(... f.slice(0, f.length / 2).map(
            (x, i) => new Multiply( f[f.length / 2 + i],new Multiply(x, Const.TWO))))
    );

    const Length = newOperator(
        (... args) => Math.sqrt(args.reduce((curSum, x) => curSum += x * x, 0)), "length",
        (... f) => (f.empty() ? Const.ZERO :
            new Multiply(new Divide( Const.HALF, new Length(... f.slice(0, f.length / 2))),
                                    Sumsq.prototype.typeDiff(... f)))
    );

    return {
        Add: Add,
        Subtract: Subtract,
        Multiply: Multiply,
        Divide: Divide,
        Negate: Negate,
        Const: Const,
        Variable: Variable,
        ArcTan: ArcTan,
        ArcTan2: ArcTan2,
        Sum: Sum,
        Avg: Avg,
        Sumsq: Sumsq,
        Length: Length
    }

})();

let Add = operators.Add;
let Subtract = operators.Subtract;
let Multiply = operators.Multiply;
let Divide = operators.Divide;
let Negate = operators.Negate;
let Const = operators.Const;
let Variable = operators.Variable;
let ArcTan = operators.ArcTan;
let ArcTan2 = operators.ArcTan2;
let Sum = operators.Sum;
let Avg = operators.Avg;
let Sumsq = operators.Sumsq;
let Length = operators.Length;

const exceptions = function() {
    function newExceptions(exception, name) {
        exception.prototype = Object.create(Error.prototype);
        exception.prototype.name = name;
        exception.prototype.constructor = exception;
    }

    let ExceptionMessage = (expected, found, position) => "expected " + expected + " ,but found '" + found + "' on position " + (position + 1).toString();

    function MissingOperandException(expected, found, position) {
        this.message = ExceptionMessage(expected, found, position);
    }
    function MissingOperatorException(expected, found, position) {
        this.message = ExceptionMessage(expected, found, position);
    }
    function WrongNumberOfArgumentsException(expected, found, position) {
        this.message = ExceptionMessage(expected, found, position);
    }
    function InvalidEndException(expected, found, position) {
        this.message = ExceptionMessage(expected, found, position);
    }

    newExceptions(MissingOperandException, "MissingOperandException");
    newExceptions(MissingOperatorException, "MissingOperatorException");
    newExceptions(WrongNumberOfArgumentsException, "WrongNumberOfArgumentsException");
    newExceptions(InvalidEndException, "InvalidEndException");

    return {MissingOperandException: MissingOperandException,
        MissingOperatorException: MissingOperatorException,
        WrongNumberOfArgumentsException: WrongNumberOfArgumentsException,
        InvalidEndException: InvalidEndException}
}();

let MissingOperandException = exceptions.MissingOperandException;
let MissingOperatorException = exceptions.MissingOperatorException;
let WrongNumberOfArgumentsException = exceptions.WrongNumberOfArgumentsException;
let InvalidEndException = exceptions.InvalidEndException;

class Tokenizer {
    constructor(exp) {
        this.exp = exp;
        this.index = 0;
        this.skipWhiteSpaces();
    }

    static tokenCharacter(c) {
        return !/\s/.test(c) && c !== "(" && c !== ")";
    }

    nextToken() {
        let res = "";
        if (this.exp[this.index] === "(") {
            res += "(";
            this.index++;
            this.skipWhiteSpaces();
        }
        while (this.index < this.exp.length && Tokenizer.tokenCharacter(this.exp[this.index])) {
            res += this.exp[this.index];
            this.index++;
        }
        this.skipWhiteSpaces();
        if (this.index < this.exp.length && this.exp[this.index] === ")") {
            res += ")";
            this.index++;
            this.skipWhiteSpaces();
        }
        return res;
    }

    getIndex() {
        return this.index;
    }

    skipWhiteSpaces() {
        while (this.index < this.exp.length && /\s/.test(this.exp[this.index])) {
            this.index++;
        }
    }
}

const parser = function() {
    let countArgs = [];
    let operations = [];
    let variables = [];
    variables["x"] = variables["y"] = variables["z"] = true;
    let addOperation = function(str, type, cnt) {
        countArgs[str] = cnt;
        operations[str] = type;
    };
    addOperation("+", Add, 2);
    addOperation("-", Subtract, 2);
    addOperation("*", Multiply, 2);
    addOperation("/", Divide, 2);
    addOperation("negate", Negate, 1);
    addOperation("atan", ArcTan, 1);
    addOperation("atan2", ArcTan2, 2);
    addOperation("sum", Sum, Infinity);
    addOperation("avg", Avg, Infinity);
    addOperation("sumsq", Sumsq, Infinity);
    addOperation("length ", Length, Infinity);

    let parse = (exp) => exp.trim().split(/\s+/).reduce(functionParse, []).pop();

    let functionParse = (stack, token) => {
        if (!isNaN(Number(token))) {
            stack.push(new Const(parseInt(token, 10)));
        } else if (variables[token] === true) {
            stack.push(new Variable(token));
        } else {
            stack.push(new operations[token](... stack.splice(-countArgs[token])));
        }
        return stack;
    };

    let parsePrefix = function(exp) {
        let tokens = new Tokenizer(exp);
        let token;
        let operator = [];
        let args = [];
        let parsedArgs = [];
        while ((token = tokens.nextToken()) !== "") {
            let openBracket = false;
            if (token[0] === "(") {
                openBracket = true;
                token = token.substring(1);
            }
            let closeBracket = false;
            if (token[token.length - 1] === ")") {
                closeBracket = true;
                token = token.substring(0, token.length - 1);
            }
            if (openBracket === true) {
                if (operations[token] === undefined) {
                    throw new MissingOperatorException("operator after (", token, tokens.getIndex() - token.length);
                }
                operator.push(token);
                parsedArgs.push(0);
            }
            if (token !== "") {
                if (!isNaN(Number(token))) {
                    args.push(new Const(parseInt(token, 10)));
                    parsedArgs[parsedArgs.length - 1]++;
                } else if (variables[token] === true) {
                    args.push(new Variable(token));
                    parsedArgs[parsedArgs.length - 1]++;
                } else if (openBracket === false) {
                    throw new MissingOperandException("Const, Variable or new SubExpression", token, tokens.getIndex() - token.length);
                }
            }
            if (closeBracket === true) {
                if (operator.empty() || parsedArgs.empty() || (countArgs[operator.last()] !== Infinity && parsedArgs.last() < countArgs[operator.last()])) {
                    throw new WrongNumberOfArgumentsException("more arguments for " + operator.last(), token + ")", tokens.getIndex() - token.length);
                }
                let parts = args.splice(-parsedArgs.last());
                args.push(new operations[operator.last()](... parts));
                operator.pop();
                parsedArgs.pop();
                if (parsedArgs.length !== 0) {
                    parsedArgs[parsedArgs.length - 1]++;
                }
            } else if (parsedArgs.last() === countArgs[operator.last()]) {
                throw new WrongNumberOfArgumentsException("end of SubExpression of " + operator.last(), token, tokens.getIndex() - token.length);
            }
        }
        if (args.length !== 1 || !operator.empty() || !parsedArgs.empty()) {
            throw new InvalidEndException("fully parsed expression", "a non-parsed arguments: " + args.join(", ") + "; operators: " + operator.join(","), tokens.getIndex() - token.length);
        }
        return args.pop();
    };

    let parsePostfix = function(exp) {
        let tokens = new Tokenizer(exp);
        let token;
        let args = [];
        let parsedArgs = [];
        while ((token = tokens.nextToken()) !== "") {
            let openBracket = false;
            if (token[0] === "(") {
                openBracket = true;
                token = token.substring(1);
            }
            let closeBracket = false;
            if (token[token.length - 1] === ")") {
                closeBracket = true;
                token = token.substring(0, token.length - 1);
            }
            if (token === "length") {
                token += " ";
            }
            if (openBracket) {
                if (operations[token] !== undefined && !closeBracket) {
                    throw new MissingOperandException("Const, Variable or new SubExpression", token, tokens.getIndex() - token.length);
                }
                parsedArgs.push(0);
            }
            if (closeBracket) {
                if (operations[token] === undefined) {
                    throw new MissingOperatorException("operator before )", token, tokens.getIndex() - token.length);
                }
                if (parsedArgs.empty() || (countArgs[token] !== Infinity && parsedArgs.last() !== countArgs[token])) {
                    throw new WrongNumberOfArgumentsException("more arguments for " + token, token + ")", tokens.getIndex() - token.length);
                }
                let parts = args.splice(-parsedArgs.last());
                args.push(new operations[token](... parts));
                parsedArgs.pop();
                if (!parsedArgs.empty()) {
                    parsedArgs[parsedArgs.length - 1]++;
                }
            }
            if (token !== "") {
                if (!isNaN(Number(token))) {
                    args.push(new Const(parseInt(token, 10)));
                    parsedArgs[parsedArgs.length - 1]++;
                } else if (variables[token] === true) {
                    args.push(new Variable(token));
                    parsedArgs[parsedArgs.length - 1]++;
                } else if (closeBracket === false) {
                    throw new MissingOperandException("Const, Variable or new SubExpression", token, tokens.getIndex() - token.length);
                }
            }
        }
        if (args.length !== 1 || parsedArgs.length > 0) {
            throw new InvalidEndException("fully parsed expression", "a non-parsed arguments: " + args.join(", "), tokens.getIndex() - token.length);
        }
        return args.pop();
    };


    return {parse: parse,
            parsePrefix: parsePrefix,
            parsePostfix: parsePostfix};
}();

let parse = parser.parse;
let parsePrefix = parser.parsePrefix;
let parsePostfix = parser.parsePostfix;

//let expr = new Add(new Const(2), new Const(1));
