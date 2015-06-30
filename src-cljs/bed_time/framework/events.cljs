(ns bed-time.framework.events
  (:require [cljs.core.async :refer [pub put! chan]]))

(defonce events-chan (chan))
(defonce events-pub (pub events-chan :handler))

(defn dispatch [event]
  (put! events-chan event))
