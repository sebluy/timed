(ns bed-time.sessions.sessions
  (:require [bed-time.util :as util]))

(defn sessions-map [] (sorted-map-by util/date-comparator))

(defn valid? [{:keys [start finish activity]}]
  (not (or (util/datetime-invalid? start)
           (and finish (util/datetime-invalid? finish))
           (nil? activity))))

(defn current? [{finish :finish}]
  (nil? finish))

(defn- find-current-in-sessions [sessions]
  (first (filter #(current? %) (vals sessions))))

(defn current [activities]
  (some #(find-current-in-sessions  %) (vals activities)))

(defn time-spent [{:keys [start finish] :as session}]
  (if (current? session)
    (util/time-diff start (js/Date.))
    (util/time-diff start finish)))

(defmulti string (fn [key _] key))

(defmethod string :start [_ value]
  (util/date->str value))

(defmethod string :finish [_ value]
  (if (nil? value)
    "Unfinished"
    (util/date->str value)))

(defmulti error (fn [key _] key))

(defmethod error :start [_ value]
  (cond (nil? value) "Start cannot be blank"
        (util/datetime-invalid? value) "Invalid Date"))

(defmethod error :finish [_ value]
  (if (and value (util/datetime-invalid? value))
    "Invalid Date"))
