# TP - FP de Backus

> Cesar Leguizamon
> 81052

# Primitivas

## Selectores

### Selector

Devuelve el "n-avo" elemento desde la izquierda de una lista, indefinido 
si no existe en la lista.

- Ambiente: una lista 
	< a, b, c, d >

- Ejemplo:
	2 : < a, b, c, d >
	b

### Cola

Devuelve todos los elementos de la lista con excepción del primero

- Ambiente: una lista 
	< a, b, c, d >

- Ejemplo:
	tl : < a, b, c, d >
	< b, c, d >

### Identidad

Devuelve el mismo elemento

- Ambiente: un objeto (un atomo o una lista)
	pepe
	< a, b, c, d >

- Ejemplo:
	id : pepe
	pepe

> El selector y la cola tienen una versión "reversa" donde se invierten, toman
> desde la derecha en lugar de desde la izquierda.

## Predicados

### Atom

Devuelve "T" si el objeto es un atomo, "F" si no

- Ambiente: un objeto (un atomo o una lista)
	pepe
	< a, b, c, d >

- Ejemplo 1:
	atom : pepe
	T

- Ejemplo 2:
	atom : < a, b, c, d >
	F

### eq

Devuelve "T" los dos objectos de una lista dada son iguales, "F" si no.

- Ambiente: una lista con dos objetos
	< a, a >

- Ejemplo:
	eq : < a, a >
	T

### null

Devuelve "T" si el objeto es la lista vacia, "F" si no.

- Ambiente: un objeto (un atomo o una lista)
	< a, a >

- Ejemplo:
	null : < a, a >
	F

## Funciones Aritmeticas

### Suma

- Ambiente: Una lista con dos atomos de tipo numerico
	< 1, 2 >

- Ejemplo:
	+ : < 1, 2 >
	3

### Resta

- Ambiente: Una lista con dos atomos de tipo numerico
	< 1, 2 >

- Ejemplo:
	- : < 1, 2 >
	-1

### Producto

- Ambiente: Una lista con dos atomos de tipo numerico
	< 1, 2 >

- Ejemplo:
	× : < 1, 2 >
	2

### Cociente

- Ambiente: Una lista con dos atomos de tipo numerico
	< 1, 2 >

- Ejemplo:
	÷ : < 1, 2 >
	0.5

## Funciones logicas

### And

Devuelve "T" si los dos atomos de la lista son "T", "F" si no.

- Ambiente: Una lista con dos atomos de tipo booleano
	< T, F >

- Ejemplo:
	and : < T, F >
	F

### Or

Devuelve "T" si al menos uno de los dos atomos de la lista son "T", "F" si no.

- Ambiente: Una lista con dos atomos de tipo booleano
	< T, F >

- Ejemplo:
	or : < T, F >
	T

### Not

Devuelve "T" si el atomo es "F" y "F" si el atomo es "T"

- Ambiente: Un atomo de tipo booleano
	T

- Ejemplo:
	not : T
	F

## Manipular secuencias

### Longitud

Devuelve la cantidad de objetos en una lista.

- Ambiente: Una lista
	< T, F>

- Ejemplo:
	length : < T, F >
	2

### Invertir

Aplicado a una lista devuelve una lista con los objetos de la primer lista en 
orden inverso.

- Ambiente: Una lista
	< T, F >

- Ejemplo:
	reverse : < T, F >
	< F, T >

### Transponer

Dada una lista con sub-listas de la misma longitud devuelve una lista con sub-listas
donde el objecto "n-esimo" de cada sub-lista original estan en la misma "n-esima" 
sub-lista del resultado; transpone la matriz representada por la lista de sub-listas.

- Ambiente: Una lista con sub-listas de la misma longitud
	< <a, b, c>, <1, 2, 3> >

- Ejemplo:
	trans: < <a, b, c>, <1, 2, 3> >
	< <a, 1>, <b, 2>, <c, 3> >

### Distribuir

Dada una lista formada por un atomo y una sub-lista devuelve una lista con 
sub-listas formadas por el atomo y un objeto de la sub-lista original.
Aplica una distributiva.

- Ambiente: Una lista con un atomo y una sub-lista
	< a, <1, 2, 3> >

- Ejemplo:
	distl: < a, <1, 2, 3> >
	< <a, 1>, <a, 2>, <a, 3> >

### Concatenar

Concatena el objeto de la izquierda, el primero, con la lista de la derecha, el
segundo.

- Ambiente: Una lista de dos objectos donde el segundo objeto sea una lista
	< b, <1, 2, 3> >

- Ejemplo:
	apnl: < b, <1, 2, 3> >
	< b, 1, 2, 3 >

### Rotar

Devuelve una lista donde el primer objecto de la lista original se encuentra al 
final.

- Ambiente: Una lista con sub-listas de la misma longitud
	< a, b, c >

- Ejemplo:
	rotl: < a, b, c >
	< b, c, a >

> **Distribuir**, **Concatenar** y **Rotar** tienen versiones donde la operación
> se ejecuta desde la derecha en lugar de la izquierda: _distr_, _apnr_ y _rotr_
> respectivamente.

## Formas funcionales

### Composición

Aplica la funciones en forma secuencial de derecha a izquierda al objecto dado

- Ejemplo:
	1 o reverse : < a, b, c >
	c

### Construcción

Devuelve una lista donde el objeto en la "n-esima" posición es el resultado de 
aplicar la función en la misma posición al objedo dado.

- Ejemplo:
	[ 1, reverse ] : < a, b, c >
	< a, < c, b, a> >

### Condición

Si el resultado de aplicar la función de la izquierda es "T" se ejecuta la función
"apuntada" por la flecha, si no se ejecuta la función despues del punto y coma.

- Ejemplo:
	atom -> id ; 2 : < a, b, c >
	b

### Constante

Define un valor constante para ser utilizado en una función

- Ejemplo:
	+ [ id, ~4 ] : 9
	13

### Inserción

Aplica la función dada al ultimo par de objectos de una lista, el resultado de 
esa aplicación es usado con el objeto a la izquierda del par, esto se repite hasta
agotar los objetos de la lista.

- Ambiente: Una lista de por lo menos dos objetos

- Ejemplo:
	/ + : < 1, 2, 4, 8 >
	15

### Aplicación a todos

Aplica la función dada a todos los objetos de una lista

- Ambiente: Una lista

- Ejemplo:
	α 3 : < < a, b, c >, < 1, 2, 3>, <q, w, e> >
	< c, 3, e >

# Ejercicios

1.a)	def max = > -> 1# ; 2#

1.b)	def max_secuencia = / max

2.a)	def pertenece = or o alpha eq o distl

3.a)	def concat = / appendl o appendr

3.b)	def agregar_vacia_fin = appendr o [ id , ~<> ]
		def insertar_no_nulos = null o 1# -> 2# ; appendl
		def quitar_vacios = / insertar_no_nulos o agregar_vacia_fin
		def perteneciente = pertenece -> 1# ; ~<>
		def interseccion = quitar_vacios o alpha perteneciente o distr
		
		Obs: Mantiene los duplicados

3.c)	def no_perteneciente = not o pertenece -> 1# ; ~<>
		def diff_l = quitar_vacios o alpha no_perteneciente o distr

3.d)	def diff_r = quitar_vacios o alpha no_perteneciente o alpha reverse o distl
		def diff = concat o [ diff_l , diff_r ]

5.e)	def profundidad =  atom -> ~0 ; + o [ ~1 , max_secuencia o alpha profundidad ]