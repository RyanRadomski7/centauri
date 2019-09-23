(ns centauri.core
  (:gen-class)
  (:require [centauri.syn.blueprints :as syn-blueprint]
            [centauri.syn.updates :as syn-update]
            [centauri.syn.synapses :as syn-synapse]
            [centauri.data.data :as data]
            [centauri.prop.forward :as prop-forward]
            [centauri.prop.backward :as prop-backward]
            [centauri.calc.delta :as calc-delta]
            [centauri.calc.error :as calc-error]
            [centauri.util :refer :all]
            [clojure.core.matrix :refer :all]
            [clojure.pprint :as pprint])
  (:import [centauri.syn.layer SimpleLayerBuilder]
           [centauri.syn.blueprints HorizontalBlueprintBuilder]
           [centauri.syn.synapses SimpleSynapseBuilder]
           [centauri.data.data SimpleData]
           [centauri.prop.forward SimplePropForward]
           [centauri.calc.delta SimpleCalcDelta]
           [centauri.calc.error SimpleCalcError]
           [centauri.prop.backward SimplePropBackward]))

(def data (SimpleData. [[0 0] [0 1] [1 0] [1 1]]
                       [[0] [1] [1] [0]]))

(def syn (syn-synapse/build-synapses
          (SimpleSynapseBuilder. (HorizontalBlueprintBuilder. 3 3)
                                 (SimpleLayerBuilder.))
          2 1))

(defn transfer [x] (/ 1 (inc (Math/exp (- x)))))

(defn dtransfer [x] (* x (- 1 x)))

(def activ (prop-forward/prop-forward (SimplePropForward. transfer)
                                      data syn))

(def deltas
  (prop-backward/prop-backward (SimplePropBackward.
                                (SimpleCalcDelta. dtransfer)
                                (SimpleCalcError.))
                               data syn activ))
