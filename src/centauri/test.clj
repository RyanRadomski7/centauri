(ns centauri.test
  (:require [centauri.config.build-fn :as build-fn]
            [centauri.syn.blueprints :as syn-blueprint]
            [centauri.syn.updates :as syn-update]
            [centauri.util :refer :all]
            [clojure.core.matrix :refer :all]
            [clojure.pprint :as pprint]))

#_(def data
    (map #(zipmap [:x :y] %)
         (mapv #(emap float %) [[[0 0] [0]]
                                [[0 1] [1]]
                                [[1 0] [1]]
                                [[1 1] [0]]])))

#_(def build-options [{:fn-name :syn-blueprint
                       :args {:num-in :eval
                              :num-hidden :int
                              :num-out :eval
                              :hidden-length :int}
                       :options {:horizontal {}
                                 :linear {}}}
                      {:fn-name :syn-update
                       :args  {:activ :eval 
                               :syn :eval
                               :deltas :eval}
                       :options {:constant {:learn-rate :float}
                                 :basic {}}}])

#_(def recipe (build-fn/build-recipe build-options))

#_(def fn-map (update-all recipe eval))

#_(defn build-synapses
    [num-left num-right]
    (->> rand
         (repeatedly (* num-left num-right))
         (partition num-right)))

#_(defn build-network
    [num-in num-out]
    (mapv (partial apply build-synapses)
          ((fn-map :syn-blueprint) num-in num-out)))

#_(defn transfer [x] (/ 1 (inc (Math/exp (- x)))))

#_(defn dtransfer [x] (* x (- 1 x)))

#_(defn prop-forward
    [data syn]
    (reductions #(emap transfer (dot %1 %2)) (map :x data) syn))

#_(defn calc-error
    [delta syn]
    (dot delta (transpose syn))    )

#_(defn calc-delta
    [error activ]
    (emap * error (emap dtransfer activ))    )

#_(defn prop-back-iter
    [delta activ syn]
    (calc-delta (calc-error delta syn) activ))

#_(defn prop-back
    [data syn activ]
    (let [init-error (emap - (map :y data) (last activ))
          init-delta (calc-delta init-error (last activ))]
      (multi-reductions-init prop-back-iter
                             init-delta
                             ((comp rest reverse rest) activ)
                             (reverse (drop 1 syn))))    )

#_(defn epoc
    [data syn]
    (let [activities (prop-forward data syn)]
      ((fn-map :syn-update)
       (prop-back data syn activities)
       activities
       syn)))

#_(defn epoc-closure
    [data]
    (fn [syn]
      (epoc data syn)))

#_(defn training-seq
    [data syn]
    (iterate (epoc-closure data) syn))

#_(defn neural-calc
    [data syn]
    (last (prop-forward data syn)))

#_(let [net (build-network 2 1)
        act (prop-forward data net)]
    (neural-calc data (nth (training-seq data net) 1000)))
