(ns bed-time.activities.list
  (:require [bed-time.activities.activities :as core]
            [bed-time.state :as state]
            [bed-time.activities.form :as form]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.activities.activities :as activities]
            [re-frame.core :as re-frame]
            [bed-time.sessions.current :as current])
  (:require-macros [reagent.ratom :as reaction]))


(defn delete-button [activity]
  [:input.btn.btn-sm.btn-danger.pull-right
   {:type     "button"
    :value    "Delete!"
    :on-click #(re-frame/dispatch [:post-delete-activity activity])}])

#_(defn session-action-button [activity]
  (let [current-session @state/current-session]
    (cond (nil? current-session)
          (sessions/start-session-button activity)
          (= activity (current-session :activity))
          (sessions/end-session-button))))

(re-frame/register-sub
  :activities
  (fn [db _]
    (reaction/reaction (@db :activities))))

(re-frame/register-sub
  :current-session
  (fn [db _]
    (let [activities (reaction/reaction (@db :activities))]
      (reaction/reaction (current/extract-current @activities)))))

(defn show-day [activity]
  ^{:key activity}
  [:tr
   [:td [:a {:href (str "/#activities/" activity)} activity]]
;   [:td (activities/weekly-time-spent activity)]
;   [:td (session-action-button activity)]
   [:td (delete-button activity)]])

(defn activities-list []
  (let [activities (re-frame/subscribe [:activities])]
    (fn []
      [:table.table
       [:thead
        [:tr [:td "Activity"]]]; [:td "Weekly Time Spent"]]]
       [:tbody
        (doall
          (for [activity (keys @activities)]
            (show-day activity)))]])))

(defn page []
  (let [current-session (re-frame/subscribe [:current-session])]
    (fn []
      [:div.col-md-6.col-md-offset-3
       [:div.page-header
        [:h1 "Activities"]]
       (if (nil? @current-session)
         [form/form])
       [activities-list]])))

