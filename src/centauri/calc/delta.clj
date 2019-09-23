(ns centauri.calc.delta
  (:require [clojure.core.matrix :refer :all]))

(defprotocol CalcDelta
  (calc-delta [calc error activ]))

(defrecord SimpleCalcDelta [dtransfer]
  CalcDelta
  (calc-delta [_ error activ]
    (emap * error (emap dtransfer activ))))
