(ns centauri.syn.blueprints)

(defn- make-blueprint
  [s]
  (partition 2 (flatten [(first s)
                         (mapv #(identity [% %]) (drop-last 1 (drop 1 s)))
                         (last s)])))

(defn- round [x] (read-string (format "%.0f" (float x))))

(defn- sloping-range
  [start finish length]
  (if (= 1 length)
    (list (round (/ (+ start finish) 2)))
    (let [slope (/ (- finish start) (dec length))]
      (map #(round (+ start (* slope %))) (range length)))))

(defprotocol LayerBlueprintBuilder
  (build-layer-blueprint [builder num-in num-out]))

(defrecord LinearBlueprintBuilder [num-hidden hidden-length]
  LayerBlueprintBuilder
  (build-layer-blueprint [_ num-in num-out]
    (make-blueprint (flatten [num-in
                              (sloping-range num-in num-out hidden-length)
                              num-out]))))

(defrecord HorizontalBlueprintBuilder [num-hidden hidden-length]
  LayerBlueprintBuilder
  (build-layer-blueprint [_ num-in num-out]
    (make-blueprint
     (flatten [num-in (repeat hidden-length num-hidden) num-out]))))
