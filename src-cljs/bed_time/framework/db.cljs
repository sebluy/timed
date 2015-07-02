(ns bed-time.framework.db
  (:require [reagent.core :as reagent]))

(defonce db (reagent/atom {}))

(defn transition [& transitions]
  (swap!
    db
    (fn [initial-db]
      (reduce (fn [new-db transition]
                (transition new-db))
              initial-db transitions))))

