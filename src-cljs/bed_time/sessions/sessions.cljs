(ns bed-time.sessions.sessions
  (:require [bed-time.util :as util]
            [ajax.core :refer [POST]]))

(defn sessions-map [] (sorted-map-by util/date-comparator))

(defn map->vec [session-map]
  [(session-map :start) (session-map :finish)])

(defn valid? [{:keys [start finish activity]}]
  (not (or (util/datetime-invalid? start)
           (util/datetime-invalid? finish)
           (nil? activity))))

(defn current? [{finish :finish}]
  (nil? finish))

(defn- find-current-in-sessions [sessions]
  (loop [sessions' sessions]
    (let [session (second (first sessions'))]
      (cond (empty? sessions') nil
            (current? session) session
            :else (recur (rest sessions'))))))

(defn current [activities]
  (loop [activities' activities]
    (let [sessions (second (first activities'))
          current (find-current-in-sessions sessions)]
      (cond (empty? activities') nil
            current current
            :else (recur (rest activities'))))))

(defn time-spent [{:keys [start finish] :as session}]
  (if (current? session)
    (util/time-diff start (js/Date.))
    (util/time-diff start finish)))

