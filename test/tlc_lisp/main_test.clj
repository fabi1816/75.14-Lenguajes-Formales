(ns tlc-lisp.main-test
  (:require [clojure.test :refer [deftest is testing]]
            [tlc-lisp.main :refer [controlar-aridad
                                   igual?
                                   actualizar-amb
                                   revisar-f
                                   revisar-lae
                                   buscar
                                   imprimir
                                   evaluar
                                   evaluar-cond
                                   evaluar-secuencia-en-cond]]))


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
    (is (= '(A B K V C D) (actualizar-amb '(A B K feo C D) 'K 'V))))
  (testing "Si hay un error no modifica el ambiente"
    (is (= '() (actualizar-amb '() 'K '*error*)))
    (is (= '(K V) (actualizar-amb '(K V) 'K '*error*)))))

(deftest test-revisar-f
  (testing "Es un error"
    (is (= '(*error*) (revisar-f '(*error*))))
    (is (= '(*error* A B) (revisar-f '(*error* A B)))))
  (testing "No es un error"
    (is (nil? (revisar-f '())))
    (is (nil? (revisar-f '(A))))
    (is (nil? (revisar-f '(A B))))))

(deftest test-revisar-lae
  (testing "No hay error en la lista"
    (is (nil? (revisar-lae '())))
    (is (nil? (revisar-lae '(A))))
    (is (nil? (revisar-lae '(A B))))
    (is (nil? (revisar-lae '(A (B C))))))
  (testing "Hay un error en la lista"
    (is (= '(*error*) (revisar-lae '((*error*)))))
    (is (= '(*error* msg) (revisar-lae '((*error* msg)))))
    (is (= '(*error* msg) (revisar-lae '(A B (*error* msg)))))
    (is (= '(*error* msg) (revisar-lae '(A B (*error* msg) C D))))
    (is (= '(*error* msg) (revisar-lae '(A B (*error* msg) (C D) E))))))

(deftest test-buscar
  (testing "Existe el elemento"
    (is (= 'B (buscar 'A '(A B))))
    (is (= 'B (buscar 'A '(X Y A B))))
    (is (= 'B (buscar 'A '(X Y A B V W)))))
  (testing "No existe el elemento"
    (is (= '(*error* unbound-symbol A) (buscar 'A '())))
    (is (= '(*error* unbound-symbol A) (buscar 'A '(C D))))
    (is (= '(*error* unbound-symbol A) (buscar 'A '(C D E F))))))

(deftest test-imprimir
  (testing "Imprimir un escalar"
    (is (= 1 (imprimir 1)))
    (is (= 'TEXTO (imprimir 'TEXTO)))
    (is (= "String" (imprimir "String")))
    (is (= \space (imprimir \space))))
  (testing "Imprimir una lista"
    (is (= '() (imprimir '())))
    (is (= '(A B) (imprimir '(A B)))))
  (testing "Es un mensaje de error"
    (is (= '(*error* descrip) (imprimir '(*error* descrip)))))
  (testing "Primer parametro es nil"
    (is (= 'hola (imprimir nil 'hola))))
  (testing "Primer parametro es una lista no-nil"
    (is (= '(C D) (imprimir '(A B) '(C D))))))

(deftest test-evaluar-cond
  (testing "TODO"
    (is (= "TODO..." (evaluar-cond nil nil nil)))))

(deftest test-evaluar-secuencia-en-cond
  (testing "TODO"
    (is (= "TODO..." (evaluar-secuencia-en-cond nil nil nil)))))

(deftest test-evaluar-escalares
  (testing "La expresion es escalar numero o string"
    (is (= '(1 (A B)) (evaluar 1 '(A B) '(C D))))
    (is (= '("TEXTO" (A B)) (evaluar "TEXTO" '(A B) '(C D)))))
  (testing "Busca el escalar en los ambientes, primero el local"
    (is (= '(B (A B)) (evaluar 'A '(A B) '(C D))))
    (is (= '(D (A B)) (evaluar 'C '(A B) '(C D))))
    (is (= '(Y (A X)) (evaluar 'A '(A X) '(A Y)))))
  (testing "La expresion es nil o lista vacia, en TLC-Lisp son lo mismo"
    (is (= '(nil (A B)) (evaluar nil '(A B) '(C D))))
    (is (= '(nil (A B)) (evaluar '() '(A B) '(C D))))))

(deftest test-evaluar-comandos-especiales
  (testing "La expresión es un mensaje de error"
    (is (= '((*error* descrip) (A B)) (evaluar '(*error* descrip) '(A B) '(C D)))))
  (testing "Evaluar comando salir"
    (is (= '(nil nil) (evaluar '(exit) '(A B) '(C D))))
    (is (= '((*error* too-many-args) (A B)) (evaluar '(exit extra) '(A B) '(C D))))))
