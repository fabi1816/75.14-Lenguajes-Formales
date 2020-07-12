(ns tlc-lisp.main-test
  (:require [clojure.test :refer [deftest is testing]]
            [tlc-lisp.main :refer [actualizar-amb
                                   aplicar
                                   buscar
                                   controlar-aridad
                                   evaluar
                                   evaluar-cond
                                   evaluar-secuencia-en-cond
                                   igual?
                                   imprimir
                                   revisar-f
                                   revisar-lae]]))

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
    (is (nil? (revisar-f 'A)))
    (is (nil? (revisar-f 'B)))
    (is (nil? (revisar-f '())))
    (is (nil? (revisar-f nil)))))

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

(deftest test-evaluar-comandos-simples
  (testing "La expresión es un mensaje de error"
    (is (= '((*error* descrip) (A B)) (evaluar '(*error* descrip) '(A B) '(C D)))))
  (testing "Evaluar comando salir"
    (is (= '(nil nil) (evaluar '(exit) '(A B) '(C D))))
    (is (= '((*error* too-many-args) (A B)) (evaluar '(exit extra) '(A B) '(C D))))))

(deftest test-evaluar-comando-setq
  (testing "Error al evaluar el comando setq"
    (is (= '((*error* list expected nil) (A B)) (evaluar '(setq) '(A B) '(C D))))
    (is (= '((*error* list expected nil) (A B)) (evaluar '(setq nil) '(A B) '(C D))))
    (is (= '((*error* list expected nil) (A B)) (evaluar '(setq ()) '(A B) '(C D))))
    (is (= '((*error* cannot-set nil) (A B)) (evaluar '(setq nil XXX) '(A B) '(C D))))
    (is (= '((*error* cannot-set nil) (A B)) (evaluar '(setq () XXX) '(A B) '(C D))))
    (is (= '((*error* symbol expected 1) (A B)) (evaluar '(setq 1 XXX) '(A B) '(C D))))
    (is (= '((*error* symbol expected "TEXTO") (A B)) (evaluar '(setq "TEXTO" XXX) '(A B) '(C D)))))
  (testing "Evaluar el comando setq"
    (is (= '(1 (A 1)) (evaluar '(setq A 1) '(A B) '(C D))))
    (is (= '(1 (A B X 1)) (evaluar '(setq X 1) '(A B) '(C D))))
    (is (= '(1 (A B C 1)) (evaluar '(setq C 1) '(A B) '(C D))))))

(deftest test-evaluar-comando-de
  (testing "Error al evaluar el comando 'de'"
    (is (= '((*error* list expected nil) (A B)) (evaluar '(de) '(A B) nil)))
    (is (= '((*error* list expected nil) (A B)) (evaluar '(de fun-nombre) '(A B) nil)))
    (is (= '((*error* list expected "XX") (A B)) (evaluar '(de fun-nombre "XX") '(A B) nil)))
    (is (= '((*error* list expected XX) (A B)) (evaluar '(de fun-nombre XX) '(A B) nil)))
    (is (= '((*error* cannot-set nil) (A B)) (evaluar '(de nil ()) '(A B) nil)))
    (is (= '((*error* cannot-set nil) (A B)) (evaluar '(de () ()) '(A B) nil)))
    (is (= '((*error* symbol expected 1) (A B)) (evaluar '(de 1 ()) '(A B) nil)))
    (is (= '((*error* symbol expected "XX") (A B)) (evaluar '(de "XX" ()) '(A B) nil))))
  (testing "Evalua el comando 'de' y define una función"
    (is (= '(f (f (lambda () (1)))) (evaluar '(de f () (1)) '() nil)))
    (is (= '(f (A B f (lambda () (1)))) (evaluar '(de f () (1)) '(A B) nil)))
    (is (= '(suma (suma (lambda (a b) (+ a b)))) (evaluar '(de suma (a b) (+ a b)) '() nil)))
    (is (= '(suma (A B suma (lambda (a b) (+ a b)))) (evaluar '(de suma (a b) (+ a b)) '(A B) nil)))))

(deftest test-evaluar-comando-quote
  (testing "Quote vacio"
    (is (= '(nil ()) (evaluar '(quote) '() nil)))
    (is (= '(nil ()) (evaluar '(quote ()) '() nil)))
    (is (= '(nil ()) (evaluar '(quote nil) '() nil)))
    (is (= '(nil (A B)) (evaluar '(quote nil) '(A B) nil))))
  (testing "Quote algo"
    (is (= '(A ()) (evaluar '(quote A) '() nil)))
    (is (= '(AB ()) (evaluar '(quote AB) '() nil)))
    (is (= '(AB (C D)) (evaluar '(quote AB) '(C D) nil)))))

(deftest test-evaluar-comando-lambda
  (testing "Errores al definir lambdas"
    (is (= '((*error* list expected nil) ()) (evaluar '(lambda) '() nil)))
    (is (= '((*error* list expected nil) (A B)) (evaluar '(lambda) '(A B) nil)))
    (is (= '((*error* list expected 1) ()) (evaluar '(lambda 1) '() nil)))
    (is (= '((*error* list expected AAA) ()) (evaluar '(lambda AAA) '() nil))))
  (testing "Lambda define función"
    (is (= '((lambda () (1)) ()) (evaluar '(lambda () (1)) '() nil)))
    (is (= '((lambda () (1)) (A B)) (evaluar '(lambda () (1)) '(A B) nil)))
    (is (= '((lambda (x) (* 2 x)) (A B)) (evaluar '(lambda (x) (* 2 x)) '(A B) nil)))))


;; (deftest test-evaluar-commando-cond
;;   (testing "Lista de comandos vacia"
;;     (is (= '(nil ()) (evaluar '(cond) '() nil)))
;;     (is (= '(nil ()) (evaluar '(cond ()) '() nil)))
;;     (is (= '(nil ()) (evaluar '(cond nil) '() nil)))
;;     (is (= '(nil (A B)) (evaluar '(cond ()) '(A B) nil)))
;;     (is (= '(nil (A B)) (evaluar '(cond ()) '(A B) '(C D)))))
;;   (testing "Cond simple en formato TLC-Lisp"
;;     (is (= '(A ()) (evaluar '(cond (t 'A)) '(t true) nil)))))

(deftest test-evaluar-secuencia-en-cond
  (testing "No hay nada para evaluar"
    (is (= nil (evaluar-secuencia-en-cond nil '() nil)))
    (is (= nil (evaluar-secuencia-en-cond '() '() nil)))
    (is (= nil (evaluar-secuencia-en-cond '(()) '() nil)))
    (is (= nil (evaluar-secuencia-en-cond '(nil) '() nil))))
  (testing "La lista solo tiene una evaluación"
    (is (= 1 (evaluar-secuencia-en-cond '(1) '() nil)))
    (is (= 'A (evaluar-secuencia-en-cond '('A) '() nil)))
    (is (= 3 (evaluar-secuencia-en-cond '((+ 1 2)) '(+ add) nil))))
  (testing "La lista tiene varias evaluaciones"
    (is (= 9 (evaluar-secuencia-en-cond '((+ 1 2) 9) '(+ add) nil)))
    (is (= 6 (evaluar-secuencia-en-cond '((+ 1 2) 9 (+ 3 3)) '(+ add) nil)))
    (is (= 'A (evaluar-secuencia-en-cond '((+ 1 2) 9 (+ 3 3) 'A) '(+ add) nil)))))

; un cond en TLC-Lisp es:
; (cond
;      ( (<True o False>) (ope_1) (ope_2) )
;      ( (<True o False>) (ope_3) (ope_4) )
;      ( True (ope_5) (ope_6) )
; )

(deftest test-aplicar
  (testing "Maneja errores en las funciones por aplicar"
    (is (= '((*error*) ()) (aplicar '(*error*) '() '() nil)))
    (is (= '((*error*) (A B)) (aplicar '(*error*) '() '(A B) nil)))
    (is (= '((*error* descrip) (A B)) (aplicar '(*error* descrip) '() '(A B) nil))))
  (testing "Maneja errores en los argumentos por aplicar"
    (is (= '((*error*) ()) (aplicar '() '((*error*)) '() nil)))
    (is (= '((*error*) (A B)) (aplicar '() '((*error*)) '(A B) nil)))
    (is (= '((*error* descrip) (A B)) (aplicar '() '((*error* descrip)) '(A B) nil))))
  (testing "Manejo de la función 'env'"
    (is (= '(() ()) (aplicar 'env '() '() nil)))
    (is (= '((A B) (A B)) (aplicar 'env '() '(A B) nil)))
    (is (= '((A B C D) (A B)) (aplicar 'env '() '(A B) '(C D)))))
  (testing "Errores de la función 'env'"
    (is (= '((*error* too-many-args) ()) (aplicar 'env '(1) '() nil)))
    (is (= '((*error* too-many-args) ()) (aplicar 'env '(A) '() nil))))
  (testing "Errores de la función 'first'"
    (is (= '((*error* too-few-args) ()) (aplicar 'first '() '() nil)))
    (is (= '((*error* list expected A) ()) (aplicar 'first '(A) '() nil))))
  (testing "Manejo de la función 'first'"
    (is (= '(nil ()) (aplicar 'first '(()) '() nil)))
    (is (= '(nil ()) (aplicar 'first '(nil) '() nil)))
    (is (= '(1 ()) (aplicar 'first '((1)) '() nil)))
    (is (= '(1 ()) (aplicar 'first '((1 2)) '() nil)))
    (is (= '(1 (A B)) (aplicar 'first '((1 2)) '(A B) nil)))
    (is (= '(1 (A B)) (aplicar 'first '((1 2)) '(A B) '(C D)))))
  (testing "Manejo de la función 'add'"
    (is (= '(3 ()) (aplicar 'add '(1 2) '() nil)))
    (is (= '(5 (A B)) (aplicar 'add '(2 3) '(A B) nil))))
  (testing "Errores de la función 'add'"
    (is (= '((*error* too-few-args) ()) (aplicar 'add '() '() nil)))
    (is (= '((*error* too-few-args) ()) (aplicar 'add '(1) '() nil)))
    (is (= '((*error* too-few-args) (A B)) (aplicar 'add '(1) '(A B) nil)))
    (is (= '((*error* number-expected) ()) (aplicar 'add '(1 X) '() nil)))
    (is (= '((*error* number-expected) ()) (aplicar 'add '(X 1) '() nil)))
    (is (= '((*error* number-expected) (A B)) (aplicar 'add '(X Y) '(A B) nil))))
  (testing "Error en aplicar funciones definidas por el usuario"
    (is (= '((*error* unbound-symbol X) ()) (aplicar 'X '() '() nil)))
    (is (= '((*error* unbound-symbol X) (A 1)) (aplicar 'X '() '(A 1) '(B 1))))
    (is (= '((*error* non-applicable-type t) (A t)) (aplicar 'A '() '(A t) nil)))
    (is (= '((*error* non-applicable-type nil) (A nil)) (aplicar 'A '() '(A nil) nil)))
    (is (= '((*error* non-applicable-type 1) (A 1)) (aplicar 'A '() '(A 1) nil)))
    (is (= '((*error* non-applicable-type 2) (A 1)) (aplicar 'B '() '(A 1) '(B 2))))
    (is (= '((*error* non-applicable-type 1) ()) (aplicar 1 '() '() nil)))
    (is (= '((*error* non-applicable-type t) ()) (aplicar 't '() '() nil)))
    (is (= '((*error* non-applicable-type nil) ()) (aplicar nil '() '() nil)))
    (is (= '((*error* random-error) ()) (aplicar '(*error* random-error) '() '() nil))))
  (testing "Aplicar funciones definidas por el usuario"
    (is (= '(3 (sumar add)) (aplicar 'sumar '(1 2) '(sumar add) nil)))
    (is (= '(5 (pepe add sumar pepe)) (aplicar 'sumar '(2 3) '(pepe add sumar pepe) nil))))
  (testing "Errores al aplicar lambdas"
    (is (= '((*error* too-few-args) (+ add))
           (aplicar '(lambda (x) (+ x x)) '() '(+ add) nil)))
    (is (= '((*error* too-many-args) (+ add))
           (aplicar '(lambda (x) (+ x x)) '(1 2) '(+ add) nil))))
  (testing "Aplicar lambdas"
    (is (= '(6 (+ add)) (aplicar '(lambda (x) (+ x x)) '(3) '(+ add) nil)))
    (is (= '(9 (+ add)) (aplicar '(lambda (x) (+ x x) (+ x x x)) '(3) '(+ add) nil)))))
