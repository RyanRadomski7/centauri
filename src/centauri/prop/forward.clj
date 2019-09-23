(ns centauri.prop.forward
  (:require [clojure.core.matrix :refer :all]
            [centauri.data.data :as data]
            [centauri.util :refer :all]))

(defprotocol PropForward
  (prop-forward [prop data syn]))

(defrecord SimplePropForward [transfer]
  PropForward
  (prop-forward [_ data syn]
    (reductions #(emap transfer (dot %1 %2)) (:input data) syn)))
