(ns bed-time.sessions.sessions
  (:require [bed-time.util :as util]
            [re-frame.core :refer [dispatch]]))

(defn sessions-map [] (sorted-map-by util/date-comparator))

(defn path->map [db activity start]
  {:activity activity
   :start    start
   :finish   (get-in db [:activities activity start])})

(defn map->vec [session-map]
  [(session-map :start) (session-map :finish)])

(defn valid? [[start finish]]
  (not (or (util/datetime-invalid? start) (util/datetime-invalid? finish))))

(defn time-spent [[start finish :as session]]
  (if (valid? session)
    (- (.getTime finish)
       (.getTime start))
    0))

(defn start-session-button [activity]
  [:input.btn.btn-sm.btn-success
   {:type     "button"
    :value    "Start Session"
    :on-click #(dispatch [:new-session activity])}])
