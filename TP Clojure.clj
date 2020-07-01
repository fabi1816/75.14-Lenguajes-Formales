;;; TP - Clojure
;;; Cesar Leguizamon
;;; 81052

;; Ejer. 11
;
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


;; Ejer. 12
;
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
