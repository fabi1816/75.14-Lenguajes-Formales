(ns tlc-lisp.main-test
  (:require [clojure.test :refer [deftest is testing]]
            [tlc-lisp.main :refer [controlar-aridad]]))


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

(deftest test-que-pasa
  (testing "Soy un genio!"
    (is (= 1 1))))
