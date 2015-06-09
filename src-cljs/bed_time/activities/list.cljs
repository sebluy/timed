(ns bed-time.activities.list
  (:require [bed-time.activities.activities :as core]
            [bed-time.state :as state]
            [bed-time.activities.form :as form]
            [bed-time.activities.session :as session]))

(defn delete-button [activity]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(core/delete activity)}])

(defn end-session-button []
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    (str "End "
                   (get-in @state/activities [:pending :activity])
                   " Session")
    :on-click #(session/end-current)}])

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
   (if @state/current-session
     [end-session-button]
     [form/form])
   [activities-list]])

