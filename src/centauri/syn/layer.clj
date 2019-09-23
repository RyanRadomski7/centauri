(ns centauri.syn.layer)

(defprotocol LayerBuilder
  (build-layer [builder num-left num-right]))

(defrecord SimpleLayerBuilder []
  LayerBuilder
  (build-layer [_ num-left num-right]
    (partition num-right (repeatedly (* num-left num-right) rand))))
