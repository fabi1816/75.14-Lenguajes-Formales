(ns tlc-lisp.main-test
  (:require [clojure.test :refer [deftest is testing]]
            [tlc-lisp.main :refer [controlar-aridad
                                   igual?
                                   actualizar-amb]]))


(deftest test-controlar-aridad
  (testing "La aridad es correcta"
    (is (= 3 (controlar-aridad '(1 2 3) 3)))
    (is (= 5 (controlar-aridad '(1 2 3 4 5) 5))))
  (testing "La aridad reportada es mayor que la real"
    (is (= (list '*error* 'too-few-args) 
           (controlar-aridad '(1 2 3) 5)))
    (is (= (list '*error* 'too-few-args)
           (controlar-aridad '(1 2 3 4 5) 6))))
  (testing "La aridad reportada es menor que la real"
    (is (= (list '*error* 'too-many-args)
           (controlar-aridad '(1 2 3) 1)))
    (is (= (list '*error* 'too-many-args)
           (controlar-aridad '(1 2 3 4) 3)))))

(deftest test-igualdad
  (testing "Los strings son case-insensitive"
    (is (igual? "pepe" "PEPE"))
    (is (igual? "PEPE" "PEPE"))
    (is (igual? "pePe" "Pepe"))
    (is (igual? "pepe" "pepe")))
  (testing "Los string diferentes son siempre diferentes"
    (is (not (igual? "uno" "dos")))
    (is (not (igual? "UNO" "DOS")))
    (is (not (igual? "Uno" "doS")))
    (is (not (igual? "uno" "DOS"))))
  (testing "La lista vacia es es igual a nil según TLC-Lisp"
    (is (igual? nil '()))
    (is (igual? '() nil)))
  (testing "Las otras comparaciones son iguales"
    (is (igual? 1 1))
    (is (igual? 'A 'A))))

(deftest test-actualizar-ambiente
  (testing "Nueva clave y valor"
    (is (= '(K V) (actualizar-amb '() 'K 'V)))
    (is (= '(one two K V) (actualizar-amb '(one two) 'K 'V))))
  (testing "Actualizar clave pre-existente"
    (is (= '(K V) (actualizar-amb '(K XXX) 'K 'V)))
    (is (= '(A B K V C D) (actualizar-amb '(A B K feo C D) 'K 'V)))))