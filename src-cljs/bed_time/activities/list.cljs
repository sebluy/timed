(ns bed-time.activities.list
  (:require [bed-time.activities.activities :as core]
            [bed-time.state :as state]
            [bed-time.activities.form :as form]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.activities.activities :as activities]))

(defn delete-button [activity]
  [:input.btn.btn-sm.btn-danger.pull-right
   {:type     "button"
    :value    "Delete!"
    :on-click #(core/delete activity)}])

(defn session-action-button [activity]
  (let [current-session @state/current-session]
    (cond (nil? current-session)
          (sessions/start-session-button activity)
          (= activity (current-session :activity))
          (sessions/end-session-button))))

(defn show-day [activity]
  ^{:key activity}
  [:tr
   [:td [:a {:href (str "/#activities/" activity)} activity]]
   [:td (activities/weekly-time-spent activity)]
   [:td (session-action-button activity)]
   [:td (delete-button activity)]])

(defn activities-list []
  [:table.table
   [:thead
    [:tr [:td "Activity"] [:td "Weekly Time Spent"]]]
   [:tbody
    (doall
      (for [activity (keys @state/activities)]
        (show-day activity)))]])

(defn page []
  [:div.col-md-6.col-md-offset-3
   [:div.page-header
    [:h1 "Activities"]]
   (if (nil? @state/current-session)
     [form/form])
   [activities-list]])

