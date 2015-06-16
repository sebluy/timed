(ns bed-time.activities.activities
  (:require [clojure.string :as string]
            [bed-time.sessions.sessions :as sessions]))

(defn coerce-activities-to-sorted [new-activities]
  (into {} (map (fn [[activity sessions]]
                  [activity (into (sessions/sessions-map) sessions)])
                new-activities)))

(defn weekly-sessions [sessions]
  (take-while #(> (.getTime (first %))
                  (- (.getTime (js/Date.))
                     (* 24 60 60 7 1000)))
              sessions))

(defn total-time-spent [sessions]
  (reduce (fn [total session]
            (+ total (sessions/time-spent session)))
          0 sessions))

(defn weekly-time-spent [sessions]
  (total-time-spent (weekly-sessions sessions)))

(defn error [activity]
  (if (string/blank? activity)
    "Activity cannot be blank"))


