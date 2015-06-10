(ns bed-time.activities.list
  (:require [bed-time.activities.activities :as core]
            [bed-time.state :as state]
            [bed-time.activities.form :as form]
            [bed-time.sessions.sessions :as session]))

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
    (for [activity (keys @state/activities)]
      (show-day activity))]])

(defn page []
  [:div.col-md-8.col-md-offset-2
   [:div.page-header
    [:h1 "Activities"]]
   (if (nil? @state/current-session)
     [form/form])
   [activities-list]])

