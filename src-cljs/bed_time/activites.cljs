(ns bed-time.activites
  (:require [reagent.core :as reagent]
            [ajax.core :as ajax]))

(defonce activities (reagent/atom #{}))

(defn delete [activity]
  (let [handler (fn [_] (swap! activities #(disj % activity)))]
    (ajax/POST "/delete-activity" {:params          {:activity activity}
                                   :handler         handler
                                   :format          :edn
                                   :response-format :edn})))

(defn get-activities []
  (let [handler (fn [incoming-activities]
                  (swap! activities #(into % incoming-activities)))]
    (ajax/GET "/activities" {:handler         handler
                             :response-format :edn})))

(defn delete-button [activity]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(delete activity)}])

(defn show-day [activity]
  ^{:key activity}
  [:tr
   [:td activity]
   [:td (delete-button activity)]])

(defn activities-list []
  [:table.table
   [:thead
    [:tr [:td "Activity"]]]
   [:tbody
    (for [activity @activities]
      (show-day activity))]])

(defn page []
  (activities-list))
