(ns centauri.prop.backward
  (:require [centauri.calc.error :as calc-error]
            [centauri.calc.delta :as calc-delta]
            [centauri.data.data :as data]
            [centauri.util :refer :all]
            [clojure.core.matrix :refer :all]))

(defprotocol PropBackward
  (prop-backward-iter [prop delta activ syn])
  (prop-backward [prop data syn activ]))

(defrecord SimplePropBackward [delta-calculator error-calculator]
  PropBackward

  (prop-backward-iter [_ delta activ syn]
    (calc-delta/calc-delta delta-calculator
                           (calc-error/calc-error error-calculator
                                                  delta
                                                  syn)
                           activ))

  (prop-backward [this data syn activ]
    (let [init-error (emap - (:output data) (last activ))
          init-delta (debug (calc-delta/calc-delta delta-calculator
                                                   init-error
                                                   (last activ)))]
      (multi-reductions-init (partial prop-backward-iter this)
                             init-delta
                             ((comp rest reverse rest) activ)
                             (reverse (drop 1 syn))))))
