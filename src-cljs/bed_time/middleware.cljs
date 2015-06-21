(ns bed-time.middleware
  (:require [re-frame.core :refer [trim-v]]))

(defn- remove-db [handler]
  (fn [db v]
    (handler v)
    db))

(def static-db (comp trim-v remove-db))

(defn remove-v [handler]
  (fn [db _]
    (handler db)))
