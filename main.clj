;; Inital problems

;; Point 1
( defn ang-ok [ a ]
  (and (< a 180) (> a 0))
)

( defn tercer-angulo [ a b ]
  ( if 
    (and (ang-ok a) (ang-ok b))
    (- 180 (+ a b ))
    (print "wrong!")
  )
)

;; Point 2
( defn segs [ d h m s]
  (+ s 
    (* m 60)
    (* h 60 60)
    (* d 24 60 60))
)

;; Point 3
(defn sig-mul-10 [ n ]
  ( if (neg? n)
    (* 10
      (quot n 10))
    (* 10
      (+ 1
        (quot n 10))))
)

;; Point 5
(defn unidad [ n ]
  (rem n 10))

(defn decena [ n ] 
  (unidad (quot n 10)))

(defn centena [ n ] 
  (decena (quot n 10)))

(defn mil [ n ] 
  (centena (quot n 10)))

(defn d-mil [ n ] 
  (mil (quot n 10)))


(defn capicua? [ n ]
  (cond
    (< n 0) (print "Too low!")
    (< n 10) true
    (< n 100) (= (decena n) (unidad n))
    (< n 1000) (= (centena n) (unidad n))
    (< n 10000) (and 
                  (= (mil n) (unidad n))
                  (= (centena n) (decena n)))
    (< n 100000) (and 
                  (= (d-mil n) (unidad n))
                  (= (mil n) (decena n)))
    true (print "Too long!"))
)

;; Point 8
(defn fib [ n ]
  (cond
    (= n 0) 0
    (= n 1) 1
    true (+ 
          (fib (- n 1)) 
          (fib (- n 2)))
    )
)

;; El apunte define los siguientes elementos
(def L (conj (conj (conj () 'c) 'b) 'a))
(def V (conj (conj (conj [] 'a) 'b) 'c))
(def Q (conj (conj (conj clojure.lang.PersistentQueue/EMPTY 'a) 'b) 'c))
;(seq Q) ; para verla 
(def HS (hash-set 'a 'b 'c))
(def SS (sorted-set 'a 'c 'b))
(def HM (hash-map :v1 'a, :v2 'b, :v3 'c))
(def SM (sorted-map :v1 'a, :v3 'c, :v2 'b))