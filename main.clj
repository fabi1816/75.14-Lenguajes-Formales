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

;; Ejer. 4
(defn traducir-nucleotido [n]
  (case n
    "G" "C"
    "C" "G"
    "T" "A"
    "A" "U"
    "?"))

(defn adn2arn [adn]
  (apply str
    (map traducir-nucleotido 
      (map str adn))))

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

;; Ejer. 14
(defn cant-nucleotidos [adn]
  (frequencies (map str adn)))

;; Ejer. 16
; TIP: Hecho por el prof
(defn cant-div [primos n]
  (if (= n 2) 0 
  (reduce + 
    (map #(- 1 (Integer/signum (rem n %))) 
      (take-while #(<= % (Math/sqrt n)) primos)))))

; TIP: Hecho por el prof
; Devuelve los primos menores o iguales que n 
(defn primes [n]
  (reduce #(if (zero? (cant-div %1 %2)) (conj %1 %2) %1) 
    (cons [] (range 2 (inc n)))))

;;; TP - entrega 16/06/2020

;; Ejer. 11 (No-recursivo)
; (triang-sup '((1 2 3) (4 5 6) (7 8 9)) )
; => ((1 2 3) (0 5 6) (0 0 9))
(defn mask [zeros ones]
  (concat (repeat zeros 0) (repeat ones 1)))

(defn cant-zeros [c]
  (range c))

(defn cant-ones [c]
  (map inc (reverse (range c))))

(defn mat-mask [M]
  (map mask 
    (cant-zeros (count M)) 
    (cant-ones (count M))))

(defn list-prod [L1 L2]
  (map * L1 L2))

(defn triang-sup [M]
  (map list-prod M (mat-mask M)))


;; Ejer. 12 (Recursivo)
; (diag '((1 2 3) (4 5 6) (7 8 9)) )
; => ((1 0 0) (0 5 0) (0 0 9))
(defn partial-mask [c]
  (cons 1 (repeat c 0)))

(defn diag-mask [c q]
  (cond
    (zero? q) '()
    true (concat (partial-mask c) (diag-mask c (dec q))) ))

(defn diag-mat-mask [M]
  (partition (count M) 
      (diag-mask (count M) (count M))))

(defn diag [M]
  (map list-prod M (diag-mat-mask M)))

;; Ejer. 20
(defn slice [string, len]
  (partition len 1 (map str string)))

;; Ejer. 25a
(defn cant-V [L]
  (count
    (filter (partial = 'V) L)))

(defn max-num-V [M]
  (reduce max (map cant-V M)))

(defn mask-VF [c max-V]
  (cond
    (= c max-V) 1
    true 0))

(defn build-mask-VF [M]
  (map
    (partial mask-VF (max-num-V M))
    (map cant-V M)))

(defn build-file-index [M]
  (map inc (range (count M))))

(defn filas-max-v [M]
  (remove zero?
    (list-prod
      (build-mask-VF M)
      (build-file-index M))))

;; Ejer. 25b
(defn cant-F [L]
  (count
    (filter (partial = 'F) L)))

(defn bool-2-VF [b]
  (case b
    true 'V
    false 'F))

(defn mas-V-o-F [M]
  (bool-2-VF
    (>
      (reduce + (map cant-V M))    ; Cant total de V
      (reduce + (map cant-F M))))) ; Cant total de F
