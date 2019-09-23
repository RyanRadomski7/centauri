(ns centauri.util
  (:require [clojure.pprint :as pprint]
            [clojure.core.matrix :refer :all]
            [clojure.walk :refer :all]))

(defn- prune-debugs
  [form]
  (if (list? form)
    (if (some #(= (first form) %) '(debug mdebug))
      (if (list? (second form))
        (map prune-debugs (second form))
        (second form))
      (map prune-debugs form))
    form))

(defmacro debug
  [form#]
  (let [pruned# (prune-debugs form#)]
    `(do (println (str (quote ~pruned#) ":"))
         (pprint/pprint ~pruned#)
         ~form#)))

(defmacro debugon
  [& forms#]
  (into forms# '(debug useon)))

(defmacro useon
  [fn# & forms#]
  (conj (map #(conj (list %) fn#) forms#)
        `do))

(defmacro mdebug
  [m#]
  (let [pruned# (prune-debugs m#)]
    `(do (println (str (quote ~pruned#) ":"))
         (pprint/pprint (emap #(format "%3.3f" %) ~pruned#))
         ~m#)))

(defn update-all
  [m & f]
  (zipmap (keys m) (map (apply partial f) (vals m))))

(defn fmap
  ([args]
   (fn [rf]
     (fn
       ([] (rf))
       ([result] (rf result))
       ([result input]
        (rf result (apply input args))))))
  ([args fs]
   (map (partial apply) fs (repeat args))))

(defn- multi-reducer-init [f]
  (fn [f1 init & colls]
    (f (partial apply f1) init (apply map list colls))))

(defn- multi-reducer [f]
  (fn [f1 & colls]
    (reduce (partial apply f1)
            (apply f1 (first colls))
            (rest colls))))

(def multi-reduce (multi-reducer reduce))
(def multi-reductions (multi-reducer reductions))
(def multi-reduce-init (multi-reducer-init reduce))
(def multi-reductions-init (multi-reducer-init reductions))
