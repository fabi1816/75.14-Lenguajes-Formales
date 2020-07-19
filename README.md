# 75.14 - Lenguajes Formales: TP-Final

## Interprete de TLC-Lisp

Un interprete de la implementación de TLC-Lisp escrito en Clojure

## Uso

Para iniciar el interprete de TLC-LISP:

- Abrir `Powershell`
- Ir a la carpeta `C:\Users\Fabi\Documents\Lenguajes Formales 2020\TP-Final\`
- Iniciar un REPL de Clojure con `lein repl`
- Iniciar el interprete de TLC-Lisp con el comando:

> `tlc-lisp.main=> (repl)`

### Ejemplos de código TLC-Lisp falso

Cargar el archivo con los ejemplos:

> `>>> (load "src/programas/ejemplo.lsp")`  
> compa

Ejecutar el código de ejemplo:

> `>>> x`  
> 1

> `>>> y`  
> 2

> `>>> (sumar x y)`  
> 3

> `>>> (restar x y)`   
> -1

> `>>> (c (list first rest list) '((1 2 3) (4 5 6) (7 8 9)))`  
> ((1 2 3) ((4 5 6) (7 8 9)) (((1 2 3) (4 5 6) (7 8 9))))

> `>>> (cargarr)`  
> `R: 55_`  
> R * 2: 110  
> nil

> `>>> (cargarR)`  
> `R: 99_`  
> R * 2: 198  
> nil

> `>>> (recorrer '(9))`  
> (9 0)

> `>>> (recorrer '(9 8 7))`  
> (9 0)  
> (8 1)  
> (7 2)  

> `>>> m`  
> (\*error* unbound-symbol m)

> `>>> (compa 1 1)`  
> 5

> `>>> m`  
> 5

> `>>> (compa 1 2)`  
> true  
> tlc-lisp.main=>  
> _;; Sale del interprete de TLC-Lisp_

### Sistema productivo de resolución falso

Cargar las 'Jarras' y el algoritmo de resolución de TLC-Lisp:

> `>>> (load "jarra.lsp")`  
> jarra8

Ejecutar resolución del problema de las jarras:

> `>>> (breadth-first bc)`  
> ~~`Ingrese el estado inicial: (0 0)_`~~  
> ~~`Ingrese el estado final:   (4 0)_`~~  
> ~~Exito!~~  
> ~~Prof ....... 11~~  
> ~~Solucion ... ((0 0) (5 0) (0 5) (5 5) (2 8) (2 0) (0 2) (5 2) (0 7) (5 7) (4 8) (4 0))~~  
> ~~t~~  
> `(0 0)_`  
> `(4 0)_`  
> nil

_Obs:_ Estado inicial y final se refiere al par de jarras utilizadas en el algoritmo 

## Comparación con TLC-Lisp real

Para iniciar el interprete real de TLC-Lisp:

- Abrir el `DOSBox`
- Configurar teclado español `keyb sp`
- Ingresar `mount c "C:\Users\Fabi\Documents\Lenguajes Formales 2020\TLC-LISP orig"`
- Ingresar `c:`
- Iniciar TLC-Lisp `lisp`

### Ejemplos de código TLC-Lisp real

Cargar el archivo con los ejemplos:

> `>>> (load "ejemplo.lsp")`  
> compa

Ejecutar el código de ejemplo:

> `>>> x`  
> 1

> `>>> y`  
> 2

> `>>> (sumar x y)`  
> 3

> `>>> (restar x y)`   
> -1

> `>>> (c (list first rest list) '((1 2 3) (4 5 6) (7 8 9)))`  
> ((1 2 3) ((4 5 6) (7 8 9)) (((1 2 3) (4 5 6) (7 8 9))))

> `>>> (cargarr)`  
> `R: 55_`  
> R * 2: 110  
> nil

> `>>> (cargarR)`  
> `R: 99_`  
> R * 2: 198
> nil

> `>>> (recorrer '(9))`  
> (9 0)

> `>>> (recorrer '(9 8 7))`  
> (9 0)  
> (8 1)  
> (7 2)

> `>>> m`  
> (\*error* unbound-symbol m)

> `>>> (compa 1 1)`  
> 5

> `>>> m`  
> 5

> `>>> (compa 1 2)`  
> C:\\>  
> _;; Sale del interprete de TLC-Lisp_

### Sistema productivo de resolución TLC-Lisp real

Cargar las 'Jarras' y el algoritmo de resolución de TLC-Lisp:

> `>>> (load "jarra.lsp")`  
> jarra8  

Resolver el problema mediante el algoritmo

> `>>> (breadth-first bc)`  
> `Ingrese el estado inicial: (0 0)_`  
> `Ingrese el estado final:   (4 0)_`  
> Exito!  
> Prof ....... 11  
> Solucion ... ((0 0) (5 0) (0 5) (5 5) (2 8) (2 0) (0 2) (5 2) (0 7) (5 7) (4 8) (4 0))  
> t

_Obs:_ Estado inicial y final se refiere al par de jarras utilizadas en el algoritmo 

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
