(ns centauri.config.build-fn
  (:require [centauri.util :refer :all]))

(defn- firstkey [m] (((comp first keys) m) m))

(defn- eval-constant
  [k]
  (case k
    :int (inc (rand-int 10))
    :float (rand 10)
    :eval))

(def ^:private pick-options
  (map (fn [option]
         (update option
                 :options
                 (comp (partial apply array-map)
                       rand-nth
                       seq)))))

(def ^:private eval-args
  (map (fn [option]
         (update option :args update-all eval-constant))))

(def ^:private eval-options
  (map (fn [option]
         (update-in option
                    [:options ((comp first keys :options) option)]
                    update-all
                    eval-constant))))

(def ^:private eval-params
  (->> #(if (= :eval %2) ((comp symbol str name) %1) %2)
       (partial apply)
       (partial map)
       map))

(def ^:private blueprint (comp pick-options eval-args eval-options))
(def ^:private fetch-common-args (map (comp seq :args)))
(def ^:private fetch-option-args (map (comp seq firstkey :options)))
(def ^:private fetch-symbols (map (partial filter symbol?)))

(def ^:private common-arg-vec (comp fetch-common-args eval-params))
(def ^:private option-arg-vec (comp fetch-option-args eval-params))
(def ^:private closure-arg-vec (comp common-arg-vec fetch-symbols (map vec)))

(def ^:private make-seq-fns (map (partial partial into [])))

(def ^:private arg-vec
  (comp (partial map (comp vec flatten))
        (partial apply map vector)))

(defn- build-blueprint [build-options] (into [] blueprint build-options))

(defn- arg-vec-lists
  [blueprint]
  (fmap [blueprint] (into [] make-seq-fns [common-arg-vec option-arg-vec])))

(def build-arg-vec (comp arg-vec arg-vec-lists))
(def build-cloure-arg-vec (partial into [] closure-arg-vec))

(def fetch-option-fn-names
  (partial map (comp symbol name first keys :options)))
(def fetch-option-ns-names (partial map (comp symbol str name :fn-name)))

(defn build-closure
  [n f cargs argsv]
  `(fn ~cargs
     (apply ~(symbol (str n "/" f)) ~argsv)))

(defn build-closures
  [blueprint]
  (map build-closure
       (fetch-option-ns-names blueprint)
       (fetch-option-fn-names blueprint)
       (build-cloure-arg-vec blueprint)
       (build-arg-vec blueprint)))

(defn build-recipe
  [build-options]
  (let [blueprint (debug (build-blueprint build-options))]
    (zipmap (map keyword (fetch-option-ns-names blueprint))
            (build-closures blueprint))))
