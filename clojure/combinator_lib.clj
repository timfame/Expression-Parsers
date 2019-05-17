(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)

(defn _show [result]
  (if (-valid? result)
    (str "-> "
         (pr-str (-value result)) " | "
         (pr-str (apply str (-tail result))))
    "!"))
(defn tabulate [parser inputs]
  (run! (fn [input] (printf "    %-10s %s\n" input (_show (parser input)))) inputs))

(defn _empty [value] (partial -return value))
(defn _char [p] (fn [[c & cs]] (if (and c (p c)) (-return c cs))))
(defn _map [f] (fn [result] (if (-valid? result)
                              (-return (f (-value result)) (-tail result)))))
(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        ((_map (partial f (-value ar)))
          ((force b) (-tail ar)))))))
(defn _either [a b]
  (fn [str] (let [ar ((force a) str)]
              (if (-valid? ar) ar ((force b) str)))))
(defn _try [fake origin]
  (fn [str] (if (not (-valid? ((force fake) str)))
              ((force origin) str))))

(defn _exception [e name is-need]
  (fn [s] (let [st (apply str s)
                er ((force e) st)]
            (if (not (or (zero? (int (first s)))
                         (= 41 (int (first s)))
                         (-valid? er)))
              (throw (ex-info (str name "Exception: ")
                                   {:place (str "..." (subs st 0 (min 5 (count st))))}))
              (if is-need er ((force (_empty nil)) st))))))

(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))
(defn +char [chars] (_char (set chars)))
(defn +char-not [chars] (_char (comp not (set chars))))
(defn +map [f parser] (comp (_map f) parser))
(def +try _try)
(def +parser _parser)

(def +ignore (partial +map (constantly (quote ignore))))
(defn iconj [coll value] (if (= value (quote ignore)) coll (conj coll value)))
(defn +seq [& ps] (reduce (partial _combine iconj) (_empty []) ps))
(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))
(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))

(defn +or [p & ps] (reduce (partial _either) p ps))
(defn +opt [p] (+or p (_empty nil)))
(defn +star [p] (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))
(defn +plus [p] (+seqf cons p (+star p)))
(defn +str [p] (+map (partial apply str) p))
(defn +string [s] (+str (apply +seq (map (comp +char str) (char-array s)))))
(def +exception _exception)

(def *ws (+ignore (+star (+char " \t\n\r"))))
(def *digit (+char "0123456789"))
(def *number
  (+map (comp Constant read-string) (+str (+seqf concat
                     (+seqf cons (+opt (+char "-")) (+plus *digit))
                     (+seqf cons (+opt (+char ".")) (+star *digit))))))
(defn *operator
  [fakes ops]
  (+map symbol (apply +or (if (empty? fakes)
                            (map +string ops)
                            (map #(+try (+string %1) (+string %2)) fakes ops)))))
(def *all-chars (mapv char (range 32 128)))
(def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))
;(def *variable (+map Variable (+str (+plus *letter))))
(def *variable (+map (comp Variable str) (+char "xyz")))
(def *exception +exception)
(defn *end-exception [p]
  (if (nil? p) (throw (ex-info "IncorrectEndException" {:place "end of expression"})) p))