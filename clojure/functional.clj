(defn variable [s] #(% s))
(def constant constantly)

(defn operation [f]
  (fn [& a] #(apply f ((apply juxt a) %))))

(defn unaryOperation [f]
  (fn [a] #(f (a %))))

(defn my-divide [& a] (reduce #(/ %1 (double %2)) a))

(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def divide (operation my-divide))
(def negate (unaryOperation #(- %)))

(defn new-op [op fir sec]
  (fn [& a] #(op (fir ((apply juxt a) %)) (sec a))))

(def avg (new-op / #(apply + %) #(count %)))
(def med (new-op nth sort #(/ (count %) 2)))

(def operations
  {'+ add '- subtract '* multiply '/ divide
   'negate negate, 'med med 'avg avg})

(defn parse [exp]
  (cond (list? exp) (apply (operations (first exp)) (map parse (rest exp)))
        (number? exp) (constant exp)
        :else (variable (str exp))))

(defn parseFunction [s]
  (parse (read-string s)))
