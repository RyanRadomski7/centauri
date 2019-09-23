(ns centauri.calc.error
  (:require [clojure.core.matrix :refer :all]))

(defprotocol CalcError
  (calc-error [calc delta syn]))

(defrecord SimpleCalcError []
  CalcError
  (calc-error [_ delta syn]
    (dot delta (transpose syn))))
