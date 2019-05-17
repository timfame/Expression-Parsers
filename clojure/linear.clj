(defn checkVector [vector]
  (and (vector? vector)
       (every? number? vector)))

(defn checkMatrix [matrix]
  (and (vector? matrix)
       (every? checkVector matrix)
       (apply = (mapv count matrix))))

(defn makeEmpty [obj]
  (if (number? obj)
    [] (if (vector? obj)
         (mapv makeEmpty obj) obj)))

(defn letsCheck [obj emp]
  (or (checkVector obj)
      (and (vector? obj)
           (apply = emp)
           (every? true? (mapv letsCheck obj emp)))))

(defn checkObject [obj]
  (letsCheck obj (makeEmpty obj)))

(defn operation [f & objects]
  {:pre [(and (not (empty? objects))
              (checkObject (vec objects)))]}
  (if (number? (first objects))
      (apply f objects)
      (apply mapv (partial operation f) objects)))

(defn commonOperation [f]
  (partial operation f))

(defn vectorOperation [f]
  (fn [& vectors]
    {:pre [(every? checkVector vectors)]}
    (apply (partial operation f) vectors)))

(defn matrixOperation [f]
  (fn [& matrix's]
    {:pre [(every? checkMatrix matrix's)]}
    (apply (partial operation f) matrix's)))

(def v+ (vectorOperation +))
(def v- (vectorOperation -))
(def v* (vectorOperation *))

(def m+ (matrixOperation +))
(def m- (matrixOperation -))
(def m* (matrixOperation *))

(def t+ (commonOperation +))
(def t- (commonOperation -))
(def t* (commonOperation *))

(defn letsMultiplyVector [a b x y]
  (let [ax (a x) ay (a y) bx (b x) by (b y)]
    (- (* ax by) (* bx ay))))

(defn vectorMultiply [a b]
  {:pre [(and (checkVector a)
              (checkVector b)
              (== (count a) (count b) 3))]}
  (vec (for [i [0 1 2]
        :let [x (mod (+ i 1) 3)
              y (mod (+ i 2) 3)]]
        (letsMultiplyVector a b x y))))

(defn transpose [a]
  {:pre [(checkMatrix a)]}
  (apply mapv vector a))

(defn letsMultiplyMatrix [f a b]
  (mapv (fn [x] (mapv #(f x %) b)) a))

(defn matrixMultiply [a b]
  {:pre [(and (checkMatrix a) (checkMatrix b)
              (== (count (a 0)) (count b)))]}
  (letsMultiplyMatrix #(apply + (mapv * %1 %2)) a (transpose b)))

(defn scalar [& vs]
  (apply + (apply v* vs)))

(defn vect [& vs]
  {:pre [(not (empty? vs))]}
  (reduce vectorMultiply vs))

(defn v*s [v & s]
  {:pre [(and (checkVector (vec s))
              (checkVector v))]}
  (mapv (partial * (apply * s)) v))

(defn m*s [m & s]
  {:pre [(checkMatrix m)]}
  (mapv #(apply v*s % s) m))

(defn m*m [& ms]
  {:pre [(not (empty? ms))]}
  (reduce matrixMultiply ms))

(defn m*v [m & vs]
  {:pre [(checkMatrix m)]}
  (mapv (partial scalar (apply v* vs)) m))