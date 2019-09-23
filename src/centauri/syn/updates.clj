(ns centauri.syn.updates
  (:require [clojure.core.matrix :refer :all])
  (:use [centauri.util]))

(defn update-syn-iter-basic
  [deltas activ syn]
  ())

(defn- update-syn-iter-constant
  [deltas activ syn learn-rate]
  (->> deltas
       (dot (transpose activ))
       (emap * learn-rate)
       (emap + syn)))

(def ^:private order-act (comp rest reverse))

(def ^:private order-syn)

(defn- calc-syn-iter-basic
  [deltas activ]
  (dot (transpose activ) deltas))

(defn calc-updates-basic
  [deltas activ syn]
  (reverse (mapv calc-syn-iter-basic deltas (order-act activ))))

(defn basic
  [deltas activ syn]
  (mapv calc-syn-iter-basic deltas (order-act activ) (order-syn syn)))

(defn constant
  [deltas activ syn learn-rate]
  (reverse (mapv update-syn-iter-constant
                 deltas
                 (rest (reverse activ))
                 (reverse syn)
                 (repeat learn-rate))))

(defn momentum
  [deltas activ syn learn-rate momentum]
  (reverse (mapv update-syn-iter-constant
                 deltas
                 (rest (reverse activ))
                 (reverse syn)
                 (repeat (* learn-rate momentum)))))
