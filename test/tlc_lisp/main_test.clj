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
  (testing "Es el string vacio igual a nil según TLC-Lisp?"
    (is (igual? nil "")))
  (testing "El igual tambien tiene que comparar simbolos (o como se llamen)"
    (is (igual? 'a 'a))
    (is (igual? 'a 'A))
    (is (igual? 'A 'A)))
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
  (testing "Busca el escalar case-insensitive-like"
    (is (= '(2 (a 2)) (evaluar 'A '(a 2) nil)))
    (is (= '(2 (A 2)) (evaluar 'a '(A 2) nil)))
    (is (= '(3 (A 2)) (evaluar 'B '(A 2) '(b 3))))
    (is (= '(3 (A 2)) (evaluar 'b '(A 2) '(B 3)))))
  (testing "La expresion es nil o lista vacia, en TLC-Lisp son lo mismo"
    (is (= '(nil (A B)) (evaluar nil '(A B) '(C D))))
    (is (= '(nil (A B)) (evaluar '() '(A B) '(C D)))))
  (testing "Un 't' es un true en TLC-Lisp"
    (is (= '(t (t t)) (evaluar 't '(t t) nil)))))

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

(deftest test-evaluar-commando-cond
  (testing "Lista de comandos vacia"
    (is (= '(nil ()) (evaluar '(cond) '() nil)))
    (is (= '(nil ()) (evaluar '(cond ()) '() nil)))
    (is (= '(nil ()) (evaluar '(cond nil) '() nil)))
    (is (= '(nil (A B)) (evaluar '(cond ()) '(A B) nil)))
    (is (= '(nil (A B)) (evaluar '(cond ()) '(A B) '(C D)))))
  (testing "Cond simple en formato TLC-Lisp"
    (is (= '(1 (t t)) (evaluar '(cond (t 1)) '(t t) nil)))
    (is (= '(2 (t t nil nil)) (evaluar '(cond (nil 1) (t 2)) '(t t nil nil) nil)))
    (is (= '(3 (t t nil nil)) (evaluar '(cond (nil 1) (nil 2) (t 3)) '(t t nil nil) nil))))
  (testing "Cond un poco mas complejo en formato TLC-Lisp"
    (is (= '(A (t t nil nil equal equal))
           (evaluar '(cond ((equal 1 1) 'A)) '(t t nil nil equal equal) nil)))
    (is (= '(B (t t nil nil equal equal))
           (evaluar '(cond ((equal 1 2) 'A) ((equal 1 1) 'B)) '(t t nil nil equal equal) nil)))
    (is (= '(C (t t nil nil equal equal))
           (evaluar '(cond ((equal 1 2) 'A) ((equal 1 2) 'B) (t 'C))
                    '(t t nil nil equal equal) nil))))
  (testing "Cond real completo"
    (is (= '(2 (t t nil nil equal equal + add))
           (evaluar '(cond ((equal 1 1) (+ 1 1))) '(t t nil nil equal equal + add) nil)))
    (is (= '(4 (t t nil nil equal equal + add))
           (evaluar '(cond ((equal 1 2) (+ 1 1)) ((equal 2 2) (+ 2 2)))
                    '(t t nil nil equal equal + add) nil)))
    (is (= '(6 (t t nil nil equal equal + add))
           (evaluar '(cond ((equal 1 2) (+ 1 1)) ((equal 1 2) (+ 2 2)) (t (+ 3 3))) 
                    '(t t nil nil equal equal + add) nil)))
    )
  )

; un cond en TLC-Lisp es:
; (cond
;      ( (<True o False>) (ope_1) (ope_2) )
;      ( (<True o False>) (ope_3) (ope_4) )
;      ( True (ope_5) (ope_6) )
; )

(deftest test-evaluar-secuencia-en-cond
  (testing "No hay nada para evaluar"
    (is (= '(nil ()) (evaluar-secuencia-en-cond nil '() nil)))
    (is (= '(nil ()) (evaluar-secuencia-en-cond '() '() nil)))
    (is (= '(nil ()) (evaluar-secuencia-en-cond '(()) '() nil)))
    (is (= '(nil ()) (evaluar-secuencia-en-cond '(nil) '() nil))))
  (testing "La lista solo tiene una evaluación"
    (is (= '(1 ()) (evaluar-secuencia-en-cond '(1) '() nil)))
    (is (= '(A ()) (evaluar-secuencia-en-cond '('A) '() nil)))
    (is (= '(3 (+ add)) (evaluar-secuencia-en-cond '((+ 1 2)) '(+ add) nil))))
  (testing "La lista tiene varias evaluaciones"
    (is (= '(9 (+ add)) (evaluar-secuencia-en-cond '((+ 1 2) 9) '(+ add) nil)))
    (is (= '(6 (+ add)) (evaluar-secuencia-en-cond '((+ 1 2) 9 (+ 3 3)) '(+ add) nil)))
    (is (= '(A (+ add)) (evaluar-secuencia-en-cond '((+ 1 2) 9 (+ 3 3) 'A) '(+ add) nil)))))

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


; Las nuevas funciones y macros que nosotros tenemos que agregar


(deftest test-evaluar-equal
  (testing "Son distintos"
    (is (= '(nil (equal equal)) (evaluar '(equal 1 2) '(equal equal) nil)))
    (is (= '(nil (equal equal)) (evaluar '(equal 'A 'B) '(equal equal) nil)))
    (is (= '(nil (equal equal)) (evaluar '(equal "A" "B") '(equal equal) nil))))
  (testing "Son iguales"
    (is (= '(t (equal equal)) (evaluar '(equal 1 1) '(equal equal) nil)))
    (is (= '(t (equal equal)) (evaluar '(equal 'A 'A) '(equal equal) nil)))
    (is (= '(t (equal equal)) (evaluar '(equal "A" "A") '(equal equal) nil)))
    (is (= '(t (equal equal)) (evaluar '(equal "a" "A") '(equal equal) nil))))
  (testing "Errores"
    (is (= '((*error* too-few-args) (equal equal)) (evaluar '(equal 1) '(equal equal) nil)))
    (is (= '((*error* too-many-args) (equal equal)) (evaluar '(equal 1 1 1) '(equal equal) nil)))))

(deftest test-evaluar-length
  (testing "Longitud de listas"
    (is (= '(0 (length length)) (evaluar '(length '()) '(length length) nil)))
    (is (= '(0 (length length)) (evaluar '(length nil) '(length length) nil)))
    (is (= '(1 (length length)) (evaluar '(length '(1)) '(length length) nil)))
    (is (= '(1 (length length)) (evaluar '(length '(A)) '(length length) nil)))
    (is (= '(2 (length length)) (evaluar '(length '(1 1)) '(length length) nil)))
    (is (= '(2 (length length)) (evaluar '(length '(A A)) '(length length) nil)))))

(deftest test-evaluar-sub
  (testing "Restas"
    (is (= '(0 (sub sub)) (evaluar '(sub 1 1) '(sub sub) nil)))
    (is (= '(3 (sub sub)) (evaluar '(sub 4 1) '(sub sub) nil)))
    (is (= '(-2 (sub sub)) (evaluar '(sub 3 5) '(sub sub) nil))))
  (testing "Errores"
    (is (= '((*error* too-few-args) (sub sub)) (evaluar '(sub 1) '(sub sub) nil)))
    (is (= '((*error* too-many-args) (sub sub)) (evaluar '(sub 1 1 1) '(sub sub) nil)))))

(deftest test-suma-resta
  (testing "Suma y resta"
    (is (= '(3 (+ add)) (evaluar '(+ 1 2) '(+ add) nil)))
    (is (= '(2 (- sub)) (evaluar '(- 3 1) '(- sub) nil)))))

(deftest test-evaluar-not
  (testing "Niega lo que tiene enfrente"
    (is (= '(t (not not)) (evaluar '(not ()) '(not not) nil)))
    (is (= '(t (not not)) (evaluar '(not nil) '(not not) nil)))
    (is (= '(nil (not not)) (evaluar '(not 't) '(not not) nil)))))

(deftest test-evaluar-comparaciones
  (testing "Menor que..."
    (is (= '(t (lt lt)) (evaluar '(lt 1 2) '(lt lt) nil)))
    (is (= '(t (lt lt)) (evaluar '(lt 5 9) '(lt lt) nil)))
    (is (= '(nil (lt lt)) (evaluar '(lt 2 1) '(lt lt) nil)))
    (is (= '(nil (lt lt)) (evaluar '(lt 9 5) '(lt lt) nil)))
    (is (= '(nil (lt lt)) (evaluar '(lt 1 1) '(lt lt) nil))))
  (testing "Menor que... error!"
    (is (= '((*error* too-few-args) (lt lt)) (evaluar '(lt 1) '(lt lt) nil)))
    (is (= '((*error* too-many-args) (lt lt)) (evaluar '(lt 1 1 1) '(lt lt) nil))))
  (testing "Mayor que..."
    (is (= '(t (gt gt)) (evaluar '(gt 2 1) '(gt gt) nil)))
    (is (= '(t (gt gt)) (evaluar '(gt 9 5) '(gt gt) nil)))
    (is (= '(nil (gt gt)) (evaluar '(gt 1 1) '(gt gt) nil)))
    (is (= '(nil (gt gt)) (evaluar '(gt 1 2) '(gt gt) nil)))
    (is (= '(nil (gt gt)) (evaluar '(gt 5 9) '(gt gt) nil))))
  (testing "Mayor que... error!"
    (is (= '((*error* too-few-args) (gt gt)) (evaluar '(gt 1) '(gt gt) nil)))
    (is (= '((*error* too-many-args) (gt gt)) (evaluar '(gt 1 1 1) '(gt gt) nil))))
  (testing "Mayor o igual que..."
    (is (= '(t (ge ge)) (evaluar '(ge 2 1) '(ge ge) nil)))
    (is (= '(t (ge ge)) (evaluar '(ge 9 5) '(ge ge) nil)))
    (is (= '(t (ge ge)) (evaluar '(ge 1 1) '(ge ge) nil)))
    (is (= '(nil (ge ge)) (evaluar '(ge 1 2) '(ge ge) nil)))
    (is (= '(nil (ge ge)) (evaluar '(ge 5 9) '(ge ge) nil))))
  (testing "Mayor o igual que... error!"
    (is (= '((*error* too-few-args) (ge ge)) (evaluar '(ge 1) '(ge ge) nil)))
    (is (= '((*error* too-many-args) (ge ge)) (evaluar '(ge 1 1 1) '(ge ge) nil)))))

(deftest test-evaluar-reverse
  (testing "Reverse una lista"
    (is (= '(() (reverse reverse)) (evaluar '(reverse '()) '(reverse reverse) nil)))
    (is (= '((1) (reverse reverse)) (evaluar '(reverse '(1)) '(reverse reverse) nil)))
    (is (= '((A) (reverse reverse)) (evaluar '(reverse '(A)) '(reverse reverse) nil)))
    (is (= '((2 1) (reverse reverse)) (evaluar '(reverse '(1 2)) '(reverse reverse) nil)))
    (is (= '((3 2 1) (reverse reverse)) (evaluar '(reverse '(1 2 3)) '(reverse reverse) nil)))
    (is (= '((3 (2) 1) (reverse reverse)) (evaluar '(reverse '(1 (2) 3)) '(reverse reverse) nil)))
    (is (= '((3 (2 4) 1) (reverse reverse)) (evaluar '(reverse '(1 (2 4) 3)) '(reverse reverse) nil)))))

(deftest test-evaluar-cons
  (testing "Cons una lista"
    (is (= '((1) (cons cons)) (evaluar '(cons '1 '()) '(cons cons) nil)))
    (is (= '((1 2) (cons cons)) (evaluar '(cons '1 '(2)) '(cons cons) nil)))
    (is (= '((1 2 3) (cons cons)) (evaluar '(cons '1 '(2 3)) '(cons cons) nil)))
    (is (= '((A B C) (cons cons)) (evaluar '(cons 'A '(B C)) '(cons cons) nil)))))

(deftest test-evaluar-null
  (testing "Es nil"
    (is (= '(t (null null)) (evaluar '(null nil) '(null null) nil)))
    (is (= '(t (null null)) (evaluar '(null ()) '(null null) nil))))
  (testing "No es nil"
    (is (= '(nil (null null)) (evaluar '(null 1) '(null null) nil)))
    (is (= '(nil (null null)) (evaluar '(null 'A) '(null null) nil)))))

(deftest test-evaluar-list
  (testing "Construir una lista"
    (is (= '(() (list list)) (evaluar '(list) '(list list) nil)))
    (is (= '((1) (list list)) (evaluar '(list 1) '(list list) nil)))
    (is (= '((1 2) (list list)) (evaluar '(list 1 2) '(list list) nil)))
    (is (= '((1 2 3) (list list)) (evaluar '(list 1 2 3) '(list list) nil)))
    (is (= '((A B C) (list list)) (evaluar '(list 'A 'B 'C) '(list list) nil)))))

(deftest test-evaluar-rest
  (testing "No hay nada para devolver"
    (is (= '(() (rest rest)) (evaluar '(rest '()) '(rest rest) nil)))
    (is (= '(() (rest rest)) (evaluar '(rest '(1)) '(rest rest) nil))))
  (testing "Devuelve algo"
    (is (= '((2) (rest rest)) (evaluar '(rest '(1 2)) '(rest rest) nil)))
    (is (= '((2 3) (rest rest)) (evaluar '(rest '(1 2 3)) '(rest rest) nil)))))

(deftest test-evaluar-terpri
  (testing "Llamar a terpri"
    (is (= '(nil (terpri terpri)) (evaluar '(terpri) '(terpri terpri) nil))))
  (testing "Cuidado con la cantidad de params"
    (is (= '((*error* too-many-args) (terpri terpri))
           (evaluar '(terpri 1) '(terpri terpri) nil)))))

(deftest test-evaluar-append
  (testing "Error en la cantidad de argumentos"
    (is (= '((*error* too-few-args) (append append))
           (evaluar '(append) '(append append) nil)))
    (is (= '((*error* too-few-args) (append append))
           (evaluar '(append '(A)) '(append append) nil)))
    (is (= '((*error* too-many-args) (append append))
           (evaluar '(append '(A) '(B) '(C)) '(append append) nil))))
  (testing "Appendear las listas"
    (is (= '(() (append append)) (evaluar '(append '() '()) '(append append) nil)))
    (is (= '((1 2) (append append)) (evaluar '(append '(1) '(2)) '(append append) nil)))
    (is (= '((1 2) (append append)) (evaluar '(append '() '(1 2)) '(append append) nil)))
    (is (= '((1 2) (append append)) (evaluar '(append '(1 2) '()) '(append append) nil)))
    (is (= '((1 1 2 2) (append append))
           (evaluar '(append '(1 1) '(2 2)) '(append append) nil)))))

(deftest test-evaluar-eval
  (testing "Eval deberia evaluar de la misma forma que 'evaluar'"
    (is (= '(t (eval eval equal equal))
           (evaluar '(eval '(equal nil '())) '(eval eval equal equal) nil)))))

(deftest test-evaluar-prin3
  (testing "Devuelve el mismo elemento que imprime"
    (is (= '(1 (prin3 prin3)) (evaluar '(prin3 1) '(prin3 prin3) nil)))
    (is (= '(A (prin3 prin3)) (evaluar '(prin3 'A) '(prin3 prin3) nil)))
    (is (= '((1 2 3) (prin3 prin3)) (evaluar '(prin3 '(1 2 3)) '(prin3 prin3) nil)))))

(deftest test-evaluar-if
  (testing "Simple 'if'"
    (is (= '(1 (if if t t nil nil)) (evaluar '(if t 1 2) '(if if t t nil nil) nil)))
    (is (= '(2 (if if t t nil nil)) (evaluar '(if nil 1 2) '(if if t t nil nil) nil))))
  (testing "Se tiene que evaluar la condición"
    (is (= '(1 (if if t t nil nil equal equal))
           (evaluar '(if (equal 'A 'A) 1 2) '(if if t t nil nil equal equal) nil)))
    (is (= '(2 (if if t t nil nil equal equal))
           (evaluar '(if (equal 'A 'B) 1 2) '(if if t t nil nil equal equal) nil))))
  (testing "Se tiene que evaluar las ramas"
    (is (= '(3 (if if t t nil nil equal equal + add))
           (evaluar '(if (equal 'A 'A) (+ 1 2) (+ 2 3))
                    '(if if t t nil nil equal equal + add) nil)))
    (is (= '(5 (if if t t nil nil equal equal + add))
           (evaluar '(if (equal 'A 'B) (+ 1 2) (+ 2 3))
                    '(if if t t nil nil equal equal + add) nil))))
  (testing "Solo se aceptan, exactamente, 3 argumentos"
    (is (= '((*error* too-few-args) (if if t t)) (evaluar '(if) '(if if t t) nil)))
    (is (= '((*error* too-few-args) (if if t t)) (evaluar '(if t) '(if if t t) nil)))
    (is (= '((*error* too-few-args) (if if t t)) (evaluar '(if t 1) '(if if t t) nil)))
    (is (= '((*error* too-many-args) (if if t t)) (evaluar '(if t 1 2 3) '(if if t t) nil)))))

(deftest test-evaluar-or
  (testing "Simple 'or'"
    (is (= '(t (or or t t nil nil)) (evaluar '(or t t) '(or or t t nil nil) nil)))
    (is (= '(t (or or t t nil nil)) (evaluar '(or t nil) '(or or t t nil nil) nil)))
    (is (= '(t (or or t t nil nil)) (evaluar '(or nil t) '(or or t t nil nil) nil)))
    (is (= '(nil (or or t t nil nil)) (evaluar '(or nil nil) '(or or t t nil nil) nil))))
  (testing "Evaluar un 'or' mas complejo"
    (is (= '(t (or or t t nil nil equal equal)) 
           (evaluar '(or (equal 1 1) t) '(or or t t nil nil equal equal) nil)))
    (is (= '(t (or or t t nil nil equal equal)) 
           (evaluar '(or (equal 1 2) t) '(or or t t nil nil equal equal) nil)))
    (is (= '(t (or or t t nil nil equal equal)) 
           (evaluar '(or (equal 1 1) nil) '(or or t t nil nil equal equal) nil)))
    (is (= '(nil (or or t t nil nil equal equal)) 
           (evaluar '(or (equal 1 2) nil) '(or or t t nil nil equal equal) nil)))
    (is (= '(t (or or t t nil nil equal equal))
           (evaluar '(or t (equal 1 1)) '(or or t t nil nil equal equal) nil)))
    (is (= '(t (or or t t nil nil equal equal)) 
           (evaluar '(or t (equal 1 2)) '(or or t t nil nil equal equal) nil)))
    (is (= '(t (or or t t nil nil equal equal))
           (evaluar '(or nil (equal 1 1)) '(or or t t nil nil equal equal) nil)))
    (is (= '(nil (or or t t nil nil equal equal)) 
           (evaluar '(or nil (equal 1 2)) '(or or t t nil nil equal equal) nil))))
   (testing "Evaluar un 'or' real"
     (is (= '(t (or or t t nil nil equal equal))
            (evaluar '(or (equal 1 1) (equal 1 1)) '(or or t t nil nil equal equal) nil)))
     (is (= '(t (or or t t nil nil equal equal))
            (evaluar '(or (equal 1 1) (equal 1 2)) '(or or t t nil nil equal equal) nil)))
     (is (= '(t (or or t t nil nil equal equal))
            (evaluar '(or (equal 1 2) (equal 1 1)) '(or or t t nil nil equal equal) nil)))
     (is (= '(nil (or or t t nil nil equal equal))
            (evaluar '(or (equal 1 2) (equal 1 2)) '(or or t t nil nil equal equal) nil))))
   (testing "Un 'or' recibe, exactamente 2 (dos) expresiones"
     (is (= '((*error* too-few-args) (or or t t nil nil))
            (evaluar '(or) '(or or t t nil nil) nil)))
     (is (= '((*error* too-few-args) (or or t t nil nil))
            (evaluar '(or t) '(or or t t nil nil) nil)))
     (is (= '((*error* too-many-args) (or or t t nil nil))
            (evaluar '(or t t t) '(or or t t nil nil) nil)))))

;; Las pruebas que estan en el enunciado del TP

(deftest test-TP-evaluar
  (is (= '(3 (+ add r 3)) (evaluar '(setq r 3) '(+ add) nil)))
  (is (= '(doble (+ add doble (lambda (x) (+ x x))))
         (evaluar '(de doble (x) (+ x x)) '(+ add) nil)))
  (is (= '(5 (+ add)) (evaluar '(+ 2 3) '(+ add) nil)))
  (is (= '((*error* unbound-symbol +) (add add)) (evaluar '(+ 2 3) '(add add) nil)))
  (is (= '(6 (+ add doble (lambda (x) (+ x x))))
         (evaluar '(doble 3) '(+ add doble (lambda (x) (+ x x))) nil)))
  (is (= '(8 (+ add r 4 doble (lambda (x) (+ x x))))
         (evaluar '(doble r) '(+ add r 4 doble (lambda (x) (+ x x))) nil)))
  (is (= '(6 (+ add)) (evaluar '((lambda (x) (+ x x)) 3) '(+ add) nil))))

(deftest test-TP-aplicar
  (is (= '((a b) (cons cons)) (aplicar 'cons '(a (b)) '(cons cons) nil)))
  (is (= '(9 (add add r 5)) (aplicar 'add '(4 5) '(add add r 5) nil)))
  (is (= '(9 (+ add r 5)) (aplicar 'add '(4 5) '(+ add r 5) nil)))
  (is (= '(8 (+ add r 4 doble (lambda (x) (+ x x))))
         (evaluar '(doble r) '(+ add r 4 doble (lambda (x) (+ x x))) nil)))
  (is (= '(8 (+ add r 4 doble (lambda (x) (+ x x))))
         (aplicar '(lambda (x) (+ x x)) '(4) '(+ add r 4 doble (lambda (x) (+ x x))) nil))))

(deftest test-TP-controlar-aridad
  (is (= '(*error* too-few-args) (controlar-aridad '(a b c) 4)))
  (is (= 4 (controlar-aridad '(a b c d) 4)))
  (is (= '(*error* too-many-args) (controlar-aridad '(a b c d e) 4))))

(deftest test-TP-igual?
  (is (= true (igual? nil 'NIL)))
  (is (= true (igual? nil "NIL")))
  (is (= true (igual? nil ())))
  (is (= true (igual? () 'NIL))))

(deftest test-TP-imprimir
  (is (= "hola" (imprimir "hola")))
  (is (= 5 (imprimir 5)))
  (is (= 'a (imprimir 'a)))
  (is (= \space (imprimir \space)))
  (is (= '(hola "mundo") (imprimir '(hola "mundo"))))
  (is (= '(*error* hola "mundo") (imprimir '(*error* hola "mundo")))))

(deftest test-TP-actualizar-amb
  (is (= '(+ add - sub x 1) (actualizar-amb '(+ add - sub) 'x 1)))
  (is (= '(+ add - sub x 3 y 2) (actualizar-amb '(+ add - sub x 1 y 2) 'x 3))))

(deftest test-TP-revisar-f
  (is (nil? (revisar-f 'doble)))
  (is (= '(*error* too-few-args) (revisar-f '(*error* too-few-args)))))

(deftest test-TP-revisar-lae
  (is (nil? (revisar-lae '(1 add first))))
  (is (= '(*error* too-many-args) (revisar-lae '(1 add (*error* too-many-args) first)))))

(deftest test-TP-buscar
  (is (= 'sub (buscar '- '(+ add - sub))))
  (is (= '(*error* unbound-symbol doble) (buscar 'doble '(+ add - sub)))))

(deftest test-TP-evaluar-cond
  (is (= '(nil (equal equal setq setq)) (evaluar-cond nil '(equal equal setq setq) nil)))
  (is (= '(nil (equal equal first first)) 
         (evaluar-cond '(((equal 'a 'b) (setq x 1))) '(equal equal first first) nil)))
  (is (= '(2 (equal equal setq setq y 2))
         (evaluar-cond '(((equal 'a 'b) (setq x 1)) ((equal 'a 'a) (setq y 2)))
                       '(equal equal setq setq) nil)))
  (is (= '(3 (equal equal setq setq y 2 z 3))
         (evaluar-cond '(((equal'a 'b) (setq x 1)) ((equal 'a 'a) (setq y 2) (setq z 3)))
                       '(equal equal setq setq) nil))))

(deftest test-TP-evaluar-secuencia-en-cond
  (is (= '(2 (setq setq y 2)) (evaluar-secuencia-en-cond '((setq y 2)) '(setq setq) nil)))
  (is (= '(3 (setq setq y 2 z 3))
         (evaluar-secuencia-en-cond '((setq y 2) (setq z 3)) '(setq setq) nil))))







