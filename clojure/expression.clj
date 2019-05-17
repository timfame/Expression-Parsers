(load-file (.getCanonicalPath (clojure.java.io/file "objects.clj")))
(load-file (.getCanonicalPath (clojure.java.io/file "combinator_lib.clj")))

(def all-operators ["+" "-" "*" "/" "**" "//" "negate"])
(defn ex [f] (*exception f "MissingOperand" true))
(declare add-sub)
(def unary
  (delay (+seqn 0 *ws
                (ex (+or (+seqn 1 (+char "(") add-sub (+char ")"))
                         (+map #(Negate (second %))
                               (+seq (*operator [] ["negate"]) (ex unary)))
                         *number *variable)) *ws
                (*exception (*operator [] all-operators) "MissingOperator" false))))
(defn some-assoc [is-left]
  (fn [a] (let [ra (if is-left a (reverse a))]
            (reduce #(apply (operations (first %2))
                            (if is-left [%1 (second %2)]
                                        [(second %2) %1]))
                    (first ra) (partition 2 (rest ra))))))
(defn abstract [next fakes ops type]
  (+map (some-assoc type) (+seqf cons (ex next)
                                 (+map (partial apply concat)
                                       (+star (+seq (*operator fakes ops) (ex next)))))))
(def pow-log (abstract unary [] ["**" "//"] false))
(def mul-div (abstract pow-log ["**" "//"] ["*" "/"] true))
(def add-sub (abstract mul-div [] ["+" "-"] true))

(defn parseObjectInfix [exp]
  (try
    (*end-exception ((+parser add-sub) exp))
      (catch clojure.lang.ExceptionInfo e
        [(.getMessage e)
        (ex-data e)])))
