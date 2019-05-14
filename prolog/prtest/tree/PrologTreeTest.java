package prtest.tree;

import alice.tuprolog.Int;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import base.Asserts;
import base.Randomized;
import base.TestCounter;
import prtest.PrologScript;

import java.util.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class PrologTreeTest extends Randomized {
    public static final Var V = new Var("V");

    protected final PrologScript prolog = new PrologScript("tree.pl");
    private final TestCounter counter = new TestCounter();
    private final boolean hard;

    public PrologTreeTest(final boolean hard) {
        Asserts.checkAssert(getClass());
        this.hard = hard;
    }

    protected void test() {
        run();
        counter.printStatus(getClass());
    }

    private void run() {
        for (int i = 0; i < 10; i++) {
            test(new Settings(hard, i, 10, 10, true));
        }
        test(new Settings(hard, 100, 10000, 100, false));
        test(new Settings(hard, 200, 10000, 0, false));
    }

    protected void test(final Settings settings) {
        new Test(settings).run();
    }


    protected static int mode(final String[] args, final Class<?> type, final String... modes) {
        if (args.length == 0) {
            System.err.println("No arguments found");
        } else if (args.length > 1) {
            System.err.println("Only one argument expected, " + args.length + " found");
        } else if (Arrays.asList(modes).indexOf(args[0]) < 0) {
            System.err.println("First argument should be one of: \"" + String.join("\", \"", modes) + "\", found: \"" + args[0] + "\"");
        } else {
            return Arrays.asList(modes).indexOf(args[0]);
        }
        System.err.println("Usage: java -ea " + type.getName() + " {" + String.join("|", modes) + "}");
        System.exit(0);
        return -1;
    }

    public static void main(final String... args) {
        new PrologTreeTest(isHard(args, PrologTreeTest.class)).test();
    }

    protected static boolean isHard(final String[] args, final Class<? extends PrologTreeTest> type) {
        return mode(args, type, "easy", "hard") == 1;
    }

    /**
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    public static class Settings {
        protected final boolean hard;
        protected final int size;
        protected final int range;
        protected final int modifications;
        protected final boolean verbose;

        public Settings(final boolean hard, final int size, final int range, final int modifications, final boolean verbose) {
            this.hard = hard;
            this.size = size;
            this.range = range;
            this.modifications = modifications;
            this.verbose = verbose;
        }

        protected void log(final String name, final String format, final Object... args) {
            if (verbose) {
                System.out.format("%10s %s%n", name, String.format(format, args));
            }
        }

        public void tick(final int i) {
            if (!verbose && i > 0 && i % 10 == 0) {
                System.out.format("    modification %s of %s%n", i, modifications);
            }
        }

        void start() {
            System.out.format("=== size = %d%n", size);
        }
    }

    class Test {
        protected final Settings settings;
        final NavigableMap<Int, Struct> expected = new TreeMap<>();
        final List<Int> keys = new ArrayList<>();
        Term actual;
        List<Runnable> actions = new ArrayList<>();
        Set<Int> removed = new HashSet<>();

        public Test(final Settings settings) {
            this.settings = settings;
            if (hard) {
                actions.addAll(List.of(this::add, this::replace, this::remove));
            }
        }

        public void run() {
            settings.start();

            for (int i = 0; i < settings.size; i++) {
                uniqueEntry();
            }

            build();

            keys.addAll(expected.keySet());
            for (int i = 0; i < 10; i++) {
                keys.add(uniqueKey());
            }

            check();

            if (!actions.isEmpty()) {
                for (int i = 0; i < settings.modifications; i++) {
                    settings.tick(i);
                    randomItem(actions).run();
                    check();
                }
            }
        }

        private void build() {
            settings.log("build", "%s", expected);
            final Struct pairs = new Struct(expected.entrySet().stream()
                    .map(e -> new Struct(",", e.getKey(), e.getValue()))
                    .toArray(Term[]::new));
            actual = call("tree_build", pairs);
        }

        protected Term call(final String name, final Term... args) {
            return prolog.solveOne(V, query(name, args));
        }

        private Struct query(final String name, final Term[] args) {
            final Term[] fullArgs = Arrays.copyOf(args, args.length + 1);
            fullArgs[args.length] = V;
//            System.out.println(new Struct(name, fullArgs));
            return new Struct(name, fullArgs);
        }

        protected void add() {
            final Map.Entry<Int, Struct> entry = uniqueEntry();
            insert(entry.getKey(), entry.getValue());
            keys.add(entry.getKey());
        }

        protected void replace() {
            if (expected.isEmpty()) {
                return;
            }
            final Int key = existingKey();
            final Struct value = randomValue();
            expected.put(key, value);
            insert(key, value);
        }

        protected void remove() {
            if (expected.isEmpty()) {
                return;
            }

            final Int key = existingKey();
            settings.log("remove", "%s", key);
            expected.remove(key);
            removed.add(key);
            actual = call("tree_remove", actual, key);
        }

        protected Int existingKey() {
            return random(expected.keySet());
        }

        protected Int random(final Set<Int> ints) {
            return ints.stream()
                    .skip(randomInt(0, ints.size()))
                    .findFirst().orElseThrow();
        }

        private void insert(final Int key, final Struct value) {
            settings.log("insert", "%s=%s", key, value);
            actual = call("tree_insert", actual, key, value);
        }

        private Map.Entry<Int, Struct> uniqueEntry() {
            final Int key = uniqueKey();
            final Struct value = randomValue();
            expected.put(key, value);
            return Map.entry(key, value);
        }

        protected Struct randomValue() {
            return new Struct(randomString(ENGLISH));
        }

        private Int randomKey() {
            return new Int(randomInt(-settings.range, settings.range));
        }

        protected Int uniqueKey() {
            while (true) {
                final Int key = randomKey();
                if (!expected.containsKey(key)) {
                    return key;
                }
            }
        }

        protected void check() {
            counter.nextTest();
            Collections.shuffle(keys, random);
            settings.log("check", "%s", expected);
//            settings.log("", "%s", actual);
            for (final Int key : keys) {
                assertCall(expected.get(key), "tree_get", actual, key);
            }
            counter.passed();
        }

        protected void assertCall(final Object value, final String name, final Term... args) {
            if (value != null) {
                if (!Objects.equals(value, call(name, args))) {
                    throw Asserts.error("%s:%n    expected `%s`,%n   actual `%s`", query(name, args), value, call(name, args));
                }
            } else {
                prolog.solveNone(V, query(name, args));
            }
        }
    }
}
