(ns centauri.syn.synapses
  (:require [centauri.syn.blueprints :as syn-blueprint]
            [centauri.syn.layer :as syn-layer]
            [centauri.util :refer :all]))

(defprotocol SynapseBuilder
  (build-synapses [syn-builder num-left num-right]))

(defrecord SimpleSynapseBuilder [blueprint-builder Layer-builder]
  SynapseBuilder
  (build-synapses [_ num-in num-out]
    (mapv (partial apply syn-layer/build-layer Layer-builder)
          (debug (syn-blueprint/build-layer-blueprint blueprint-builder
                                                      num-in
                                                      num-out)))))
