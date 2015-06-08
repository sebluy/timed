(ns bed-time.activities.list
  (:require [bed-time.activities.core :as core]
            [bed-time.activities.form :as form]))

(defn delete-button [activity]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(core/delete activity)}])

(defn show-day [activity]
  ^{:key activity}
  [:tr
   [:td [:a {:href (str "/#activities/" activity)} activity]]
   [:td (delete-button activity)]])

(defn activities-list []
  [:table.table
   [:thead
    [:tr [:td "Activity"]]]
   [:tbody
    (for [activity (keys @core/activities)]
      (show-day activity))]])

(defn page []
  [:div.col-md-8.col-md-offset-2
   [form/form]
   [activities-list]])

