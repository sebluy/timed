(ns bed-time.activities.activities
  (:require [ajax.core :as ajax]
            [bed-time.state :as state]
            [bed-time.sessions.current :as current-session]
            [clojure.string :as string]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.util :as util]))

(defn delete [activity]
  (let [handler (fn [_] (swap! state/activities #(dissoc % activity)))]
    (ajax/POST "/delete-activity" {:params          {:activity activity}
                                   :handler         handler
                                   :format          :edn
                                   :response-format :edn})))

(defn coerce-activities-to-sorted [new-activities]
  (into {} (map (fn [[activity sessions]]
                  [activity (into (sessions/sessions-map) sessions)])
                new-activities)))

(defn get-activities []
  (let [handler (fn [incoming-activities]
                  (reset! state/activities
                          (coerce-activities-to-sorted incoming-activities))
                  (current-session/extract-current))]
    (ajax/GET "/activities" {:handler         handler
                             :response-format :edn})))

(defn weekly-time-spent [activity]
  (let [sessions (@state/activities activity)
        millis (reduce #(+ %1 (sessions/time-spent %2)) 0 sessions)]
    (util/hours-str millis)))

(defn error [activity]
  (if (string/blank? activity)
    "Activity cannot be blank"))


