(ns tlc-lisp.extra-tests
  (:require [clojure.test :refer [deftest is testing]]
            [tlc-lisp.main :refer [evaluar]] :reload-all))


;; Para la carga del archivo de ejemplo


(def amb-cargado '(+ add - sub null null setq setq  de de if if nil nil
                     cons cons first first rest rest list list quote quote
                     prin3 prin3 read read terpri terpri cond cond t t
                     equal equal exit exit lambda lambda
                     sumar (lambda (a b) (+ a b))
                     restar (lambda (a b) (- a b)) x 1 y 2
                     C (lambda (LF X) (if (null LF) nil (cons ((first LF) X) (C (rest LF) X))))
                     cargarR (lambda () (prin3 "R: ") (setq R (read)) (prin3 "R * 2: ") (prin3 (+ R R)) (terpri))
                     recorrer (lambda (L) (recorrer2 L 0))
                     recorrer2 (lambda (L i) (COND ((NULL (rest L)) (list (first L) i)) (T (prin3 (list (first L) i)) (setq D (+ i 1)) (terpri) (recorrer2 (REST L) D))))
                     compa (lambda (a b) (if (equal a b) (setq m 5) (exit)))))

(def amb-entrada '(+ add - sub null null setq setq  de de if if
                     nil nil cons cons first first rest rest
                     list list quote quote prin3 prin3 read read
                     terpri terpri cond cond t t equal equal
                     exit exit lambda lambda))

(deftest test-cargar-arch
  (testing "Vamos a cargar el archivo de ejemplo"
    (is (= (list 'compa amb-cargado)
           (evaluar '(load "src/programas/ejemplo.lsp")
                    amb-entrada nil)))))

(deftest test-mf
  (testing "Check that these work"
    (is (= (list 1 amb-entrada)
           (evaluar '((lambda (a b) (a b)) first '(1 2)) amb-entrada nil)))
    (is (= (list 1 amb-entrada)
           (evaluar '((lambda (a b) (a b)) first (list 1 2)) amb-entrada nil))))
  (testing "First elemental"
    (is (= '(1 (first first list list))
           (evaluar '(first (list 1 2)) '(first first list list) nil)))))
