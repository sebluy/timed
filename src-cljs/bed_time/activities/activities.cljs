(ns bed-time.activities.activities
  (:require [ajax.core :as ajax]
            [bed-time.state :as state]
            [bed-time.sessions.sessions :as sessions]))

(defn delete [activity]
  (let [handler (fn [_] (swap! state/activities #(dissoc % activity)))]
    (ajax/POST "/delete-activity" {:params          {:activity activity}
                                   :handler         handler
                                   :format          :edn
                                   :response-format :edn})))

(defn get-activities []
  (let [handler (fn [incoming-activities]
                  (println "getting activities")
                  (reset! state/activities incoming-activities)
                  (sessions/extract-current))]
    (ajax/GET "/activities" {:handler         handler
                             :response-format :edn})))


