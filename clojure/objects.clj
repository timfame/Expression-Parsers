(defn any-args [f] #(reduce f %&))
(def my-divide (any-args #(/ %1 (double %2))))
(def my-pow (any-args #(Math/pow (double %1) (double %2))))
(def my-log (any-args #(/ (Math/log (Math/abs %2))
                          (Math/log (Math/abs %1)))))
(defn very [f v] (f (f v)))

(definterface Expression
  (^Number toEval [vars])
  (^String toStr [])
  (^Object toDiff [x])
  (^String toInfix []))

(deftype JConstant [a]
  Expression
  (toEval [this _] (.a this))
  (toStr [this] (if (integer? (.a this))
                  (str (.a this))
                  (format "%.1f" (.a this))))
  (toDiff [_ _] (JConstant. 0))
  (toInfix [this] (.toStr this)))
(defn Constant [a] (JConstant. a))

(def zero (Constant 0))
(def one (Constant 1))
(def neg (Constant -1))
(def two (Constant 2))

(deftype JVariable [a]
  Expression
  (toEval [this vars] (vars (.a this)))
  (toStr [this] (.a this))
  (toDiff [this var] (if (= var (.a this)) one zero))
  (toInfix [this] (.toStr this)))
(defn Variable [a] (JVariable. a))

(deftype JAbstract [f ch args dif]
  Expression
  (toEval [this vars]
    (apply (.f this) (map #(.toEval % vars) (.args this))))
  (toStr [this]
    (str "(" (clojure.string/join " " (cons (.ch this) (map #(.toStr %) (.args this)))) ")"))
  (toDiff [this var]
    (second (apply (.dif this)
                   (map list (.args this)
                        (map #(.toDiff % var) (.args this))))))
  (toInfix [this]
    (let [inf (map #(.toInfix %) (.args this)) c (.ch this)]
      (if (> (count inf) 1)
        (str "(" (first inf) " " c " " (second inf) ")")
        (str c "(" (first inf) ")")))))

(defn JOperation [f ch dif] #(JAbstract. f ch %& (any-args dif)))

(declare Multiply)
(defn JUnary [f ch dif]
  #(JAbstract. f ch %& (fn [[x dx]] (list () (Multiply (dif x) dx)))))

(def Add (JOperation + '+ (fn [[_ dx] [_ dy]] (list () (Add dx dy)))))
(def Subtract (JOperation - '- (fn [[_ dx] [_ dy]] (list () (Subtract dx dy)))))
(def Multiply
  (JOperation * '* (fn [[x dx] [y dy]]
                     (list (Multiply x y) (Add (Multiply dx y) (Multiply x dy))))))
(def Divide
  (JOperation my-divide '/
              (fn [[x dx] [y dy]]
                (list (Divide x y)
                      (Divide (Subtract (Multiply dx y) (Multiply x dy))
                              (Multiply y y))))))

(def Pow (JOperation my-pow '** (list () ())))
(def Log (JOperation my-log (symbol "//") (list () ())))

(def Negate (JUnary - 'negate (constantly neg)))
(declare Cosh)
(defn Sinh [a] ((JUnary #(Math/sinh %) 'sinh Cosh) a))
(def Cosh (JUnary #(Math/cosh %) 'cosh Sinh))

(defn evaluate [this vars] (.toEval this vars))
(defn toString [this] (.toStr this))
(defn diff [this var] (.toDiff this var))
(defn toStringInfix [this] (.toInfix this))

(def operations
  {'+ Add '- Subtract '* Multiply '/ Divide 'negate Negate
   (symbol "//") Log '** Pow})

(defn parsePrefix [exp]
  (cond (list? exp) (apply (operations (first exp))
                           (map parsePrefix (rest exp)))
        (number? exp) (Constant exp)
        :else (Variable (str exp))))

(defn parseObject [exp]
  (parsePrefix (read-string exp)))
