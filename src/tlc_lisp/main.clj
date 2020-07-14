(ns tlc-lisp.main    ; Namespace del interprete TLC-Lisp
  (:require [clojure.string :refer [ends-with? lower-case]]
            [clojure.java.io :refer [reader]]))

(declare buscar)    ; Done!
(declare igual?)    ; Done!
(declare aplicar)    ; Done!
(declare evaluar)    ; TODO: Terminar
(declare imprimir)    ; Done!
(declare revisar-f)    ; Done!
(declare revisar-lae)    ; Done!
(declare cargar-arch)    ; Done!
(declare evaluar-cond)
(declare actualizar-amb)    ; Done!
(declare controlar-aridad)    ; Done!
(declare evaluar-secuencia-en-cond)    ; Done!

;; Funciones auxiliares al final del archivo
(declare salir)
(declare error?)
(declare fun-env)
(declare fun-not)
(declare fun-sub)
(declare escalar?)
(declare fun-cons)
(declare fun-eval)
(declare fun-list)
(declare fun-null)
(declare fun-read)
(declare fun-rest)
(declare fun-equal)
(declare fun-first)
(declare fun-prin3)
(declare fun-sumar)
(declare rama-true)
(declare tlc-true?)
(declare evaluar-de)
(declare evaluar-if)
(declare evaluar-or)
(declare fun-append)
(declare fun-length)
(declare fun-terpri)
(declare rama-false)
(declare fun-reverse)
(declare next-lambda)
(declare cargar-input)
(declare evaluar-setq)
(declare cuerpo-lambda)
(declare evaluar-quote)
(declare fun-less-than)
(declare no-aplicable?)
(declare evaluar-lambda)
(declare lambda-simple?)
(declare build-amb-lambda)
(declare fun-greater-than)
(declare numero-o-string?)
(declare build-nombre-arch)
(declare de-define-params?)
(declare evaluar-escalares)
(declare aplicar-fun-lambda)
(declare evaluar-setq-unico)
(declare non-nil-empty-list?)
(declare setq-insuficientes?)
(declare lambda-define-param?)
(declare resultado-de-evaluar)
(declare aplicar-fun-escalares)
(declare evaluar-setq-multiples)
(declare nombre-archivo-valido?)
(declare aplicar-fun-lambda-simple)
(declare fun-greater-or-equal-than)
(declare aplicar-fun-lambda-multiple)
(declare fun-definida-por-el-usuario)


; REPL (read–eval–print loop).
; Aridad 0: Muestra mensaje de bienvenida y se llama recursivamente con
;  el ambiente inicial.
; Aridad 1: Muestra >>> y lee una expresion y la evalua.
; Si la 2da. posicion del resultado es nil, retorna true (caso base de la
;  recursividad).
; Si no, imprime la 1ra. pos. del resultado y se llama recursivamente con
;  la 2da. pos. del resultado. 
(defn repl
  "Inicia el REPL de TLC-Lisp"
  ([]
   (println "Interprete de TLC-LISP en Clojure")
   (println "Trabajo Practico de 75.14/95.48 - Lenguajes Formales 2020")
   (println "Inspirado en:")
   (println "TLC-LISP Version 1.51 for the IBM Personal Computer")
   (println "Copyright (c) 1982, 1983, 1984, 1985 The Lisp Company") (flush)
   (repl '(add add append append cond cond cons cons de de env env equal equal
               eval eval exit exit first first ge ge gt gt if if lambda lambda
               length length list list load load lt lt nil nil not not null null
               or or prin3 prin3 quote quote read read rest rest reverse reverse
               setq setq sub sub t t terpri terpri + add - sub)))
  ([amb]
   (print ">>> ") (flush)
   (try
     (let [res (evaluar (read) amb nil)]    ; Bindea `res` al resultado de `evaluar`
       (if (nil? (second res))              ; Chequea si la segunda posicion de `res` es `nil`
         true
         (do (imprimir (first res))         ; Imprime el primer elemento del resultado 
             (repl (second res)))))         ; Se llama a si mismo con el resto del resultado
     (catch Exception e
       (println) (print "*error* ")
       (println (get (Throwable->map e) :cause))
       (repl amb)))))


; Carga el contenido de un archivo.
; Aridad 3: Recibe los ambientes global y local y el nombre de un archivo
; (literal como string o atomo, con o sin extension .lsp, o el simbolo ligado
;  al nombre de un archivo en el ambiente), abre el archivo 
; y lee un elemento de la entrada (si falla, imprime nil), lo evalua y llama
;  recursivamente con el (nuevo?) amb., nil, la entrada y un arg. mas: el
;  resultado de la evaluacion.
; Aridad 4: lee un elem. del archivo (si falla, imprime el ultimo resultado),
;  lo evalua y llama recursivamente con el (nuevo?) amb., nil, la entrada y
;  el resultado de la eval.
(defn cargar-arch
  "Carga el contenido de un archivo"
  ([amb-global amb-local arch]
   (let [nomb (first (evaluar arch amb-global amb-local))]
     (if (error? nomb)
       (do (imprimir nomb) amb-global)    ; Mostrar el error
       (let [nom (build-nombre-arch (lower-case (str nomb)))
             ret (try (with-open [in (java.io.PushbackReader. (reader nom))]
                        (binding [*read-eval* false]
                          (cargar-input in amb-global)))
                      (catch java.io.FileNotFoundException _
                        (imprimir (list '*error* 'file-open-error 'file-not-found nom '1 'READ)) amb-global))]
         ret))))
  ([amb-global _amb-local in res]
   (try (let [res (evaluar (read in) amb-global nil)]    ; Identico a cargar-input pero maneja la excapción diferente
          (cargar-arch (second res) nil in res))
        (catch Exception _
          (imprimir (first res)) amb-global))))


; Evalua una expresion usando los ambientes global y local.
;  Siempre retorna una lista con un resultado y un ambiente.
; Si la evaluacion falla, el resultado es una lista con '*error* como primer
;  elemento, por ejemplo: (list '*error* 'too-many-args) y el ambiente es el 
;  ambiente global.
; Si la expresion es un escalar numero o cadena, retorna la expresion y el
;  ambiente global.
; Si la expresion es otro tipo de escalar, la busca (en los ambientes local y
;  global) y retorna el valor y el ambiente global.
; Si la expresion es una secuencia nula, retorna nil y el ambiente global.
; Si el primer elemento de la expresion es '*error*, retorna la expresion y el
;  ambiente global.
; Si el primer elemento de la expresion es una forma especial o una macro,
;  valida los demas elementos y retorna el resultado y el (nuevo?) ambiente.
; Si no lo es, se trata de una funcion en posicion de operador (es una
;  aplicacion de calculo lambda), por lo que se llama a la funcion aplicar,
; pasandole 4 argumentos: la evaluacion del primer elemento, una lista con las
;  evaluaciones de los demas, el ambiente global y el ambiente local.


(defn evaluar
  "Evalua una expresion en los ambientes global y local
   Retorna un lista con el resultado y un ambiente"
  [expre amb-global amb-local]
  (cond
    (escalar? expre) (evaluar-escalares expre amb-global amb-local)
    (igual? expre nil) (list nil amb-global)
    (igual? (first expre) '*error*) (list expre amb-global)
    (igual? (first expre) 'de) (evaluar-de expre amb-global amb-local)
    (igual? (first expre) 'if) (evaluar-if expre amb-global amb-local)
    (igual? (first expre) 'or) (evaluar-or expre amb-global amb-local)
    (igual? (first expre) 'exit) (salir expre amb-global amb-local)
    (igual? (first expre) 'setq) (evaluar-setq expre amb-global amb-local)
    (igual? (first expre) 'cond) (evaluar-cond (next expre) amb-global amb-local)
    (igual? (first expre) 'quote) (evaluar-quote expre amb-global amb-local)
    (igual? (first expre) 'lambda) (evaluar-lambda expre amb-global amb-local)
    :else (aplicar (resultado-de-evaluar (first expre) amb-global amb-local)         ; Función a evaluar
                   (map #(resultado-de-evaluar % amb-global amb-local) (next expre)) ; Lista de argumentos
                   amb-global amb-local)))


; TODO: Estos macros deberia ser definidos en la función evaluar
; de: Done!
; exit: Done!
; setq: Done!
; quote: Done!
; lambda: Done!
; nil: Done!
; t: Ya deberia estar... no?
; if: Done!
; or: Done!
; load: 
; 
; cond: WIP


; Aplica una funcion a una lista de argumentos evaluados, usando los ambientes
;  global y local. Siempre retorna una lista con un resultado y un ambiente.
; Si la aplicacion falla, el resultado es una lista con '*error* como primer
;  elemento, por ejemplo: (list '*error* 'arg-wrong-type) y el ambiente es el 
;  ambiente global.
; Aridad 4: Recibe la func., la lista de args. evaluados y los ambs. global y
;  local. Se llama recursivamente agregando 2 args.: la func. revisada y la 
;  lista de args. revisada.
; Aridad 6: Si la funcion revisada no es nil, se la retorna con el amb. global.
; Si la lista de args. evaluados revisada no es nil, se la retorna con el
;  amb. global.
; Si no, en caso de que la func. sea escalar (predefinida o definida por el
;  usuario), se devuelven el resultado de su aplicacion (controlando la aridad)
;   y el ambiente global.
; Si la func. no es escalar, se valida que la cantidad de parametros y
;  argumentos coincidan, y:
; en caso de que se trate de una func. lambda con un solo cuerpo, se la evalua
;  usando el amb. global intacto y el local actualizado con los params.
;  ligados a los args.,  
; en caso de haber multiples cuerpos, se llama a aplicar recursivamente,
;  pasando la funcion lambda sin el primer cuerpo, la lista de argumentos evaluados,
; el amb. global actualizado con la eval. del 1er. cuerpo (usando el amb.
;  global intacto y el local actualizado con los params. ligados a los args.)
;   y el amb. local intacto. 
(defn aplicar
  "Aplica a la lista de argumentos `lae` la función `f` en los ambientes datos"
  ([f lae amb-global amb-local]
   (aplicar (revisar-f f) (revisar-lae lae) f lae amb-global amb-local))
  ([resu1 resu2 f lae amb-global amb-local]
   (cond
     (not (nil? resu1)) (list resu1 amb-global)    ; La función es un mensaje de error
     (not (nil? resu2)) (list resu2 amb-global)    ; La lista de argumentos es un mensaje de error
     (not (seq? f)) (aplicar-fun-escalares f lae amb-global amb-local)
     :else (aplicar-fun-lambda f lae amb-global amb-local))))


(defn aplicar-fun-escalares
  "Aplica las funciones escalares estandares o las definidas por el usuario"
  [f lae amb-global amb-local]
  (cond
    (igual? f 'add) (list (fun-sumar lae) amb-global)
    (igual? f 'append) (list (fun-append lae) amb-global)
    (igual? f 'cons) (list (fun-cons lae) amb-global)
    (igual? f 'env) (list (fun-env lae amb-global amb-local) amb-global)
    (igual? f 'equal) (list (fun-equal lae) amb-global)
    (igual? f 'eval) (fun-eval lae amb-global amb-local)
    (igual? f 'first) (list (fun-first lae) amb-global)
    (igual? f 'ge) (list (fun-greater-or-equal-than lae) amb-global)
    (igual? f 'gt) (list (fun-greater-than lae) amb-global)
    (igual? f 'length) (list (fun-length lae) amb-global)
    (igual? f 'list) (list (fun-list lae) amb-global)
    (igual? f 'lt) (list (fun-less-than lae) amb-global)
    (igual? f 'not) (list (fun-not lae) amb-global)
    (igual? f 'null) (list (fun-null lae) amb-global)
    (igual? f 'prin3) (list (fun-prin3 lae) amb-global)
    (igual? f 'read) (list (fun-read lae) amb-global)
    (igual? f 'rest) (list (fun-rest lae) amb-global)
    (igual? f 'reverse) (list (fun-reverse lae) amb-global)
    (igual? f 'sub) (list (fun-sub lae) amb-global)
    (igual? f 'terpri) (list (fun-terpri lae) amb-global)
    :else (fun-definida-por-el-usuario f lae amb-global amb-local)))


; Controla la aridad (cantidad de argumentos de una funcion).
; Recibe una lista y un numero. Si la longitud de la lista coincide con el
;  numero, retorna el numero.
; Si es menor, retorna (list '*error* 'too-few-args).
; Si es mayor, retorna (list '*error* 'too-many-args).
(defn controlar-aridad
  "Devuelve la aridad de la lista si es la esperada,
   Si no devuelve una lista con un mensaje de error"
  [lis val-esperado]
  (cond
    (= val-esperado (count lis)) val-esperado
    (> val-esperado (count lis)) (list '*error* 'too-few-args)
    (< val-esperado (count lis)) (list '*error* 'too-many-args)))


; Compara la igualdad de dos simbolos.
; Recibe dos simbolos a y b. Retorna true si se deben considerar iguales;
;  si no, false.
; Se utiliza porque TLC-LISP no es case-sensitive y ademas no distingue entre
;  nil y la lista vacia.
(defn igual?
  "Compara igualdad al estilo de TLC-Lisp
   Case-insensitive y nil es igual a la lista vacia"
  [a b]
  (cond
    (and (string? a) (string? b)) (= (lower-case a) (lower-case b))
    (and (nil? a) (non-nil-empty-list? b)) true
    (and (nil? b) (non-nil-empty-list? a)) true
    :else (= a b)))


; Imprime, con salto de linea, atomos o listas en formato estandar (las cadenas
;  con comillas) y devuelve su valor. Muestra errores sin parentesis
; Aridad 1: Si recibe un escalar, lo imprime con salto de linea en formato
;  estandar (pero si es \space no lo imprime), purga la salida y devuelve
;  el escalar.
; Si recibe una secuencia cuyo primer elemento es '*error*, se llama
;  recursivamente con dos argumentos iguales: la secuencia recibida
; Si no, imprime lo recibido con salto de linea en formato estandar, purga
;  la salida y devuelve la cadena.
; Aridad 2: Si el primer parametro es nil, imprime un salto de linea, purga
;  la salida y devuelve el segundo parametro.
; Si no, imprime su primer elemento en formato estandar, imprime un espacio y
;  se llama recursivamente con la cola del primer parametro y el segundo intacto.
(defn imprimir
  "Imprime, con un salto de linea al final, lo recibido devolviendo el mismo valor,
   Muestra los errores."
  ([elem]
   (cond
     (= \space elem) elem    ; Si es \space no lo imprime pero si lo devuelve
     (and (list? elem) (= '*error* (first elem))) (imprimir elem elem)
     :else (do (println elem) (flush) elem)))    ; Es un no *error*
  ([lis orig]
   (cond
     (nil? lis) (do (println) (flush) orig)
     :else (do (print (first lis))
               (print \space)
               (imprimir (next lis) orig)))))


; Actualiza un ambiente (una lista con claves en las posiciones impares
;  [1, 3, 5...] y valores en las pares [2, 4, 6...]
; Recibe el ambiente, la clave y el valor.
; Si el valor no es escalar y en su primera posicion contiene '*error*, retorna
;  el ambiente intacto.
; Si no, coloca la clave y el valor en el ambiente (puede ser un alta o una
;  actualizacion) y lo retorna.
(defn actualizar-amb
  "Actualiza el ambiente con la clave (nombre de la función) y su valor (el 
   responsable de ejecutar esa función)
   Retorna el ambiente actualizado"
  [amb-global clave valor]
  (cond
    ; No modifica el ambiente si el valor es un *error*
    (= '*error* valor) amb-global

    ; El primer elemento es la clave buscada: Lo reemplazo junto con su valor
    (= clave (first amb-global)) (concat (list clave valor) (drop 2 amb-global))

    ; El ambiente está vacio: los agrego
    (empty? amb-global) (list clave valor)

    ; Agrego los primeros 2 elementos a lo que me devuelva la actualización
    ;  del resto del ambiente
    :else (concat (take 2 amb-global)
                  (actualizar-amb (drop 2 amb-global) clave valor))))


; Revisa una lista que representa una funcion.
; Recibe la lista y, si esta comienza con '*error*, la retorna.
;  Si no, retorna nil.
(defn revisar-f
  "Si la `lis` contiene un error lo devuelve, si no devuelve nil"
  [lis]
  (cond
    (and (seq? lis) (= '*error* (first lis))) lis
    :else nil))


; Revisa una lista de argumentos evaluados.
; Recibe la lista y, si esta contiene alguna sublista que comienza con
;  '*error*, retorna esa sublista. Si no, retorna nil.
(defn revisar-lae
  "Retorna la sub-lista que es un mensaje de error
   Si no hay ninguna retorna nil"
  [lis]
  (first (remove nil? (map revisar-f (filter seq? lis)))))


; Busca una clave en un ambiente (una lista con claves en las posiciones
;  impares [1, 3, 5...] y valores en las pares [2, 4, 6...] y retorna
;  el valor asociado
; Si no la encuentra, retorna una lista con '*error* en la 1ra. pos.,
;  'unbound-symbol en la 2da. y el elemento en la 3ra
(defn buscar
  "Busca el valor asociado al `elem` en la `lis`
   Devuelve un mensaje de error si no la encuentra"
  [elem lis]
  (cond
    (empty? lis) (list '*error* 'unbound-symbol elem)
    (= elem (first lis)) (second lis)
    :else (buscar elem (drop 2 lis))))


; Evalua el cuerpo de una macro COND. Siempre retorna una lista con un
;  resultado y un ambiente.
; Recibe una lista de sublistas (cada una de las cuales tiene una condicion
;  en su 1ra. posicion) y los ambientes global y local
; Si la lista es nil, el resultado es nil y el ambiente retornado es el global.
; Si no, evalua (con evaluar) la cabeza de la 1ra. sublista y, si el resultado
;  no es nil, retorna el res. de invocar a evaluar-secuencia-en-cond con la
;  cola de esa sublista
; En caso contrario, sigue con las demas sublistas.
(defn evaluar-cond
  [lis amb-global amb-local]
  (cond
    (igual? lis nil) (list nil amb-global)
    :else (evaluar (first lis) amb-global amb-local)))


; Evalua (con evaluar) secuencialmente las sublistas de una lista y retorna
;  el valor de la ultima evaluacion.
(defn evaluar-secuencia-en-cond
  "Evalua secuencialmente las sublistas de `lis`.
   Retorna el valor de la ultima evaluacion."
  [lis amb-global amb-local]
  (cond
    (igual? lis nil) nil
    (= (count lis) 1) (first (evaluar (first lis) amb-global amb-local))
    :else (do
            (first (evaluar (first lis) amb-global amb-local))
            (evaluar-secuencia-en-cond (next lis) amb-global amb-local))))


;; Funciones auxiliares


(defn error?
  "Es una secuencia cuyo primer elemento es '*error*'?"
  [elem]
  (and (seq? elem)
       (igual? (first elem) '*error*)))


(defn nombre-archivo-valido?
  "Checkquea que el string sea un nombre de archivo .lsp valido"
  [nombre]
  (and (> (count nombre) 4)
       (ends-with? nombre ".lsp")))


(defn cargar-input
  "Carga y evalua uno a uno todo el contenido del input"
  [input amb-global]
  (try
    (let [res (evaluar (read input) amb-global nil)]
      (cargar-arch (second res) nil input res))
    (catch Exception _
      (imprimir nil) amb-global)))


(defn non-nil-empty-list?
  "Chequea que el parametro sea una lista vacia no-nil"
  [l]
  (and (list? l) (empty? l)))


(defn escalar?
  "Chequea si `elem` es un escalar no-nulo"
  [elem] (and (not (seq? elem)) (not (nil? elem))))


(defn numero-o-string?
  "Chequea si `elem` es un numero o un string"
  [elem] (or (number? elem) (string? elem)))


(defn evaluar-escalares
  "Evalua las expresiones que son escalares"
  [expre amb-global amb-local]
  (cond
    (numero-o-string? expre) (list expre amb-global)
    :else (list (buscar expre (concat amb-local amb-global)) amb-global)))


(defn salir
  "Sale del interprete de TLC-Lisp"
  [expre amb-global _]
  (cond
    (< (count (next expre)) 1) (list nil nil)
    :else (list (list '*error* 'too-many-args) amb-global)))


(defn setq-insuficientes?
  "Valida que el comando de setq tenga suficientes elementos"
  [cmd]
  (or (= (count cmd) 1) (< (count (next cmd)) 2)))


(defn evaluar-setq-unico
  "Evalua una unica expresion del comando setq de TLC-Lisp"
  [expre amb-global amb-local]
  (let [res (evaluar (first (nnext expre)) amb-global amb-local)]
    (list
     (first res)
     (actualizar-amb amb-global (second expre) (first res)))))


(defn evaluar-setq-multiples
  "Evaluan todos los comandos de setq en la `expre`"
  [expre amb-global amb-local]
  (let [res (evaluar (first (nnext expre)) amb-global amb-local)]
    (evaluar
     (cons 'setq (drop 3 expre))
     (actualizar-amb amb-global (second expre) (first res)) amb-local)))


(defn evaluar-setq
  "Valida y ejecuta todos los bindeos de setq; el comando de TLC-Lisp"
  [expre amb-global amb-local]
  (cond
    (setq-insuficientes? expre) (list (list '*error* 'list 'expected nil) amb-global)
    (igual? (second expre) nil) (list (list '*error* 'cannot-set nil) amb-global)    ; Trata de re-definir el nil
    (not (symbol? (second expre))) (list (list '*error* 'symbol 'expected (second expre)) amb-global)
    (= (count (next expre)) 2) (evaluar-setq-unico expre amb-global amb-local)    ; Solo hay un comando setq
    :else (evaluar-setq-multiples expre amb-global amb-local)))    ; Multiples setq en la expresión


(defn de-define-params?
  "Indica si la expresión 'de' de TLC-Lisp define una lista de parametros.
   La lista de parametros puede estar vacia."
  [expre]
  (and (not (igual? (first (nnext expre)) nil))    ; No tiene una lista vacia
       (not (seq? (first (nnext expre))))))        ; No tiene una lista


(defn evaluar-de
  "Evalua la expresión 'de' en TLC-Lisp.
   Define la función en el ambiente global"
  [expre amb-global _]
  (cond
    (< (count (next expre)) 2) (list (list '*error* 'list 'expected nil) amb-global)
    (de-define-params? expre) (list (list '*error* 'list 'expected (first (nnext expre))) amb-global)
    (igual? (second expre) nil) (list (list '*error* 'cannot-set nil) amb-global)
    (not (symbol? (second expre))) (list (list '*error* 'symbol 'expected (second expre)) amb-global)
    :else (list (second expre) (actualizar-amb amb-global (second expre) (cons 'lambda (nnext expre))))))


(defn evaluar-quote
  "Evalua el comando 'quote'"
  [expre amb-global _]
  (if (igual? (second expre) nil)
    (list nil amb-global)
    (list (second expre) amb-global)))


(defn lambda-define-param?
  "Indica si la expresión lambda define correctamente sus parametros"
  [expre]
  (and (not (igual? (second expre) nil))
       (not (seq? (second expre)))))


(defn evaluar-lambda
  "Evalua el comando lambda"
  [expre amb-global _]
  (cond
    (< (count (next expre)) 1) (list (list '*error* 'list 'expected nil) amb-global)
    (lambda-define-param? expre) (list (list '*error* 'list 'expected (second expre)) amb-global)
    :else (list expre amb-global)))


(defn resultado-de-evaluar
  "Devuelve el resultado de evaluar `x` en los ambientes global y local"
  [x amb-global amb-local]
  (first (evaluar x amb-global amb-local)))


(defn fun-env
  "Devuelve la union de los ambientes global y local."
  [lae amb-global amb-local]
  (if (> (count lae) 0)
    (list '*error* 'too-many-args)
    (concat amb-global amb-local)))


(defn fun-first
  "Devuelve el primer elemento de una lista"
  [lae]
  (let [ari (controlar-aridad lae 1)]
    (cond
      (error? ari) ari
      (igual? (first lae) nil) nil
      (not (seq? (first lae))) (list '*error* 'list 'expected (first lae))
      :else (ffirst lae))))


(defn fun-sumar
  "Suma los elementos de la lista.
   Minimo 2 elementos"
  [lae]
  (if (< (count lae) 2)
    (list '*error* 'too-few-args)
    (try (reduce + lae)
         (catch Exception _ (list '*error* 'number-expected)))))


(defn no-aplicable?
  "Chequea si `elem` es un número, 't' (true), nil o una lista vacia"
  [elem]
  (or (number? elem) (igual? elem 't) (igual? elem nil)))


(defn fun-definida-por-el-usuario
  "Aplica la función definida por el usuario si esta ya existe
   en el ambiente global o local"
  [f lae amb-global amb-local]
  (let [lamb (buscar f (concat amb-local amb-global))]
    (cond
      (no-aplicable? lamb) (list (list '*error* 'non-applicable-type lamb) amb-global)
      (no-aplicable? f) (list (list '*error* 'non-applicable-type f) amb-global)
      (error? lamb) (list lamb amb-global)
      :else (aplicar lamb lae amb-global amb-local))))


(defn cuerpo-lambda
  "Dada una expresión lambda `f` devuelve su cuerpo"
  [f] (first (nnext f)))


(defn build-amb-lambda
  "Usa los parametros del lambda `f`, sus argumentos y los concatena al 
   ambiente local para construir el ambiente donde se evaluará"
  [f lae amb-local]
  (concat (flatten (map list (second f) lae)) amb-local))


(defn aplicar-fun-lambda-simple
  "Evalua un lambda `f` con un cuerpo simple"
  [f lae amb-global amb-local]
  (evaluar (cuerpo-lambda f) amb-global (build-amb-lambda f lae amb-local)))


(defn next-lambda
  "Construye una expresión lambda con el resto de las funciones del cuerpo de
   una función lambda `f`"
  [f] (cons 'lambda (cons (second f) (next (nnext f)))))


(defn aplicar-fun-lambda-multiple
  "Evalua un lambda `f` cuyo cuerpo contiene varias funciones"
  [f lae amb-global amb-local]
  (aplicar (next-lambda f)
           lae
           (second (aplicar-fun-lambda-simple f lae amb-global amb-local))    ; Nuevo ambiente global
           amb-local))


(defn lambda-simple?
  "Indica si la expresión lambda `f` tiene un cuerpo de una sola 'función'"
  [f] (nil? (next (nnext f))))


(defn aplicar-fun-lambda
  "Aplica las funciones lambdas `f` a la lista de argumentos `lae`."
  [f lae amb-global amb-local]
  (cond
    (< (count lae) (count (second f))) (list '(*error* too-few-args) amb-global)
    (> (count lae) (count (second f))) (list '(*error* too-many-args) amb-global)
    (lambda-simple? f) (aplicar-fun-lambda-simple f lae amb-global amb-local)
    :else (aplicar-fun-lambda-multiple f lae amb-global amb-local)))


(defn fun-equal
  "Compara dos elementos, si son iguales devuelve 't'
   Si son distintos 'nil'"
  [largs]
  (let [aridad (controlar-aridad largs 2)]
    (cond
      (error? aridad) aridad
      (igual? (first largs) (second largs)) 't
      :else nil)))


(defn fun-length
  "Devuelve la longitud de una lista"
  [lae] (count (first lae)))


(defn fun-sub
  "Resta dos elementos"
  [lae]
  (let [ari (controlar-aridad lae 2)]
    (if (error? ari)
      ari
      (- (first lae) (second lae)))))


(defn fun-not
  "Niega el argumento"
  [lae]
  (cond
    (igual? (first lae) nil) 't
    (= (first lae) 't) nil))


(defn fun-less-than
  "Devuelve 't si el primer numero es menor que el segundo,
   nil si no."
  [lae]
  (let [ari (controlar-aridad lae 2)]
    (cond
      (error? ari) ari
      (< (first lae) (second lae)) 't
      :else nil)))


(defn fun-greater-than
  "Devuelve 't si el primer numero es mayor que el segundo,
   nil si no."
  [lae]
  (let [ari (controlar-aridad lae 2)]
    (cond
      (error? ari) ari
      (> (first lae) (second lae)) 't
      :else nil)))


(defn fun-greater-or-equal-than
  "Devuelve 't si el primer numero es mayor o igual que el segundo,
   nil si no."
  [lae]
  (let [ari (controlar-aridad lae 2)]
    (cond
      (error? ari) ari
      (>= (first lae) (second lae)) 't
      :else nil)))


(defn fun-reverse
  "Devuelve una lista con los elementos de `lae` en orden inverso"
  [lae] (reverse (first lae)))


(defn fun-cons
  "Retorna inserción del elem en la cabeza de la lista"
  [lae] (cons (ffirst lae) (second (first lae))))


(defn fun-null
  "Retorna t si un elemento es nil"
  [lae] (if (nil? (first lae)) 't nil))


(defn fun-list
  "Retorna una lista formada por los args."
  [lae] lae)


(defn fun-rest
  "Retorna una lista sin su 1ra. posición"
  [lae] (rest (first lae)))


(defn fun-terpri
  "Imprime un salto de línea y retorna nil"
  [lae]
  (let [ari (controlar-aridad lae 0)]
    (cond
      (error? ari) ari
      :else (newline))))


(defn fun-append
  "Retorna la fusión de dos listas"
  [lae]
  (let [ari (controlar-aridad lae 2)]
    (cond
      (error? ari) ari
      :else (concat (first lae) (second lae)))))


(defn fun-eval
  "Retorna la evaluación de una lista en TLC-Lisp"
  [lae amb-global amb-local]
  (evaluar (first lae) amb-global amb-local))


(defn fun-prin3
  "Imprime un elemento y lo retorna"
  [lae]
  (print (first lae))
  (first lae))


(defn fun-read
  "Retorna la lectura de un elemento desde input standard"
  [_] (read))


(defn rama-true 
  "Devuelve la rama verdadera de una `expre`"
  [expre] (first (nnext expre)))


(defn rama-false
  "Devuelve la rama falsa de una `expre`"
  [expre] (second (nnext expre)))


(defn evaluar-if
  "Evalua una expresión `expre` condicional"
  [expre amb-global amb-local]
  (let [ari (controlar-aridad expre 4)
        resul-expre (evaluar (second expre) amb-global amb-local)]
    (cond
      (error? ari) (list ari amb-global)
      (igual? (first resul-expre) nil) (evaluar (rama-false expre) amb-global amb-local)
      :else (evaluar (rama-true expre) amb-global amb-local))))


(defn tlc-true?
  "Devuelve 'true' si el resultado de evaluar la expresión TLC-Lisp `expre`
   es 't'"
  [expre amb-global amb-local]
  (igual? (first (evaluar expre amb-global amb-local)) 't))


(defn evaluar-or
  "Evalua una expresión `expre` condicional 'or' de TLC-Lisp"
  [expre amb-global amb-local]
  (let [ari (controlar-aridad expre 3)]
    (cond
      (error? ari) (list ari amb-global)
      (or (tlc-true? (first (next expre)) amb-global amb-local)
          (tlc-true? (second (next expre)) amb-global amb-local)) (list 't amb-global)
      :else (list nil amb-global))))


(defn build-nombre-arch
  "Dada una entrada la convierte en un nombre de archivo TLC-Lisp valido"
  [nom]
  (cond
    (nombre-archivo-valido? nom) nom
    :else (str nom ".lsp")))    ; Agrega '.lsp' al final)


; Al terminar de cargar el archivo, se retorna true.

; TODO: Falta hacer que la carga del interprete en Clojure (tlc-lisp.clj) retorne true
