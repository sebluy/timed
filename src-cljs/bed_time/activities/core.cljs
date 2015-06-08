(ns bed-time.activities.core
  (:require [reagent.core :as reagent]
            [ajax.core :as ajax]))

(defonce activities (reagent/atom {}))

(defn delete [activity]
  (let [handler (fn [_] (swap! activities #(dissoc % activity)))]
    (ajax/POST "/delete-activity" {:params          {:activity activity}
                                   :handler         handler
                                   :format          :edn
                                   :response-format :edn})))

(defn get-activities []
  (let [handler (fn [incoming-activities]
                  (println incoming-activities)
                  (reset! activities incoming-activities))]
    (ajax/GET "/activities" {:handler         handler
                             :response-format :edn})))

(defn update-session [{:keys [activity start finish] :as session}]
  (let [handler (fn [_] (swap! activities #(assoc % activity {start finish})))]
    (ajax/POST "/update-session" {:params {:session session}
                                  :handler handler
                                  :format :edn
                                  :response-format :edn})))

