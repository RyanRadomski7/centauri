(ns centauri.epoc.epoc
  (:require [centauri.prop.forward :as prop-forward]
            [centauri.prop.backward :as prop-backward]
            [centauri.syn.updates :as syn-update]))

(defprotocol Epoc
  (epoc [epoc-type data syn])
  (epoc-closure [epoc-type data]))

(defrecord SimpleEpoc [syn-updater backward-proper forward-proper]
  Epoc
  (epoc [data syn]
    (let [activities (prop-forward/prop-forward forward-proper data syn)]
      (syn-update/syn-update
       (prop-back data syn activities)
       activities
       syn))))
