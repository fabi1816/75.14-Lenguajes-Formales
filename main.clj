;; Ejercicios

;; Ejer. 1
(defn abs [n] 
  (cond
    (neg? n) (- n)
    true n))

(defn char-2-int [c]
  (Character/digit c 10))

(defn digs [n]
  (map char-2-int
    (flatten 
      (partition 1 (str (abs n))))))

;; Ejer. 2
(defn texto [x]
  (str "Uno para " x ", uno para mi"))

(defn uno 
  ([] (texto "ti"))
  ([& args] (map texto args)))

;; Ejer. 3
(defn separar [L]
  (map second 
    (partition 2 L)))

(defn pares [L M]
  (concat (separar L) (separar M)))

;; Ejer. 5
(defn sacar [n L]
  (remove (partial = n) L))

; Solución del prof.
(defn elim [n L]
  (cond
    (empty? L) L    ; Está vacia
    (coll? (first L))    ; El primer elemento es una colección
                      (cons
                        (elim n (first L))  ; Elimino el n de la cabeza de la coleción
                        (elim n (rest L)))  ; Elimino el n del resto de la colección
    (= n (first L))   (elim n (rest L))   ; Ignoro el primer elemento y elimino el n del resto de la colección
    true              (cons (first L) (elim n (rest L)))))  ; Agrego el primer elemento al resto de la lista con el n eliminado

;; Ejer. 7
(defn mitad-idx [L]
  (quot (count L) 2))

(defn half [L]
  (cond
    (odd? (count L)) (nth L (mitad-idx L))
    true (list 
            (nth L (- (mitad-idx L) 1)) 
            (nth L (mitad-idx L)))))

;; Ejer. 13
(defn trans [LL]
  (list
    (map first LL)
    (map second LL))
)

; Solución del prof.
(defn trans-prof [LL]
  (apply map list LL))
  
