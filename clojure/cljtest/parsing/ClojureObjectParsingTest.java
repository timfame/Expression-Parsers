package cljtest.parsing;

import cljtest.ClojureScript;
import cljtest.multi.MultiTests;
import cljtest.object.ClojureObjectExpressionTest;
import jstest.Engine;
import jstest.Language;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ClojureObjectParsingTest extends ClojureObjectExpressionTest {
    public static final ClojureScript.F<String> TO_INFIX = ClojureScript.function("toStringInfix", String.class);
    public static final ClojureScript.F<String> TO_SUFFIX = ClojureScript.function("toStringSuffix", String.class);

    public static final Dialect INFIX = dialect(
            "%s",
            "%s.0",
            (op, args) -> {
                switch (args.size()) {
                    case 1: return op + "(" + args.get(0) + ")";
                    case 2: return "(" + args.get(0) + op + args.get(1) + ")";
                    default: throw new AssertionError("Unsupported op " + op + "/" + args.size());
                }
            }
    );
    public static final Dialect SUFFIX = dialect(
            "%s",
            "%s.0",
            (op, args) -> "("+ String.join(" ", args) + " " + op + ")"
    );
    private final boolean hard;

    protected ClojureObjectParsingTest(final Language language, final boolean hard) {
        super(language);
        this.hard = hard;
    }

    protected ClojureObjectParsingTest(final boolean hard) {
        this(new Language(PARSED, hard ? INFIX : SUFFIX, new MultiTests(false)), hard);
    }

    protected void testToString(final String expression, final String expected) {
        engine.parse(expression);
        final Engine.Result<String> result = engine.toString(hard ? TO_INFIX : TO_SUFFIX);
        assertEquals(result.context, expected, result.value);
    }

    @Override
    protected String parse(final String expression) {
        final String f = hard ? "parseObjectInfix" : "parseObjectSuffix";
        return "(" + f + " \"" + expression + "\")";
    }

    public static void main(final String... args) {
        new ClojureObjectParsingTest(mode(args, ClojureObjectParsingTest.class)).run();
    }
}
