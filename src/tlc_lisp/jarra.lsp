;;; TLC-LISTP
;;; Archivo principal - Jarra

(load 'breadth)

(setq bc '(
  (lambda (x) (if (lt (jarra5 x) 5) (list 5 (jarra8 x)) x))
  (lambda (x) (if (gt (jarra5 x) 0) (list 0 (jarra8 x)) x))
  (lambda (x) (if (ge (- 5 (jarra5 x)) (jarra8 x)) (list (+ (jarra5 x) (jarra8 x)) 0) x))
  (lambda (x) (if (lt (- 5 (jarra5 x)) (jarra8 x)) (list 5 ( - (jarra8 x) (- 5 (jarra5 x)))) x))
  (lambda (x) (if (lt (jarra8 x) 8) (list (jarra5 x) 8) x))
  (lambda (x) (if (gt (jarra8 x) 0) (list (jarra5 x) 0) x))
  (lambda (x) (if (ge (- 8 (jarra8 x)) (jarra5 x)) (list 0 (+ (jarra8 x) (jarra5 x))) x))
  (lambda (x) (if (lt (- 8 (jarra8 x)) (jarra5 x)) (list (- (jarra5 x) (- 8 (jarra8 x))) 8) x))))
                  
(de jarra5 (x) (first x))
(de jarra8 (x) (first (rest x)))