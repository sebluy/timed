(ns bed-time.activities.list
  (:require [bed-time.activities.form.components :as form]
            [re-frame.core :refer [subscribe dispatch]]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.activities.activities :as activities]
            [bed-time.util :as util]))

(defn- delete-button [activity]
  [:input.btn.btn-sm.btn-danger.pull-right
   {:type     "button"
    :value    "Delete!"
    :on-click #(dispatch [:post-delete-activity activity])}])

(defn session-action-button [activity]
  (let [current-session (subscribe [:current-session])]
    (fn []
      (cond (nil? @current-session)
            (sessions/start-session-button activity)
            (= activity (@current-session :activity))
            (sessions/end-session-button @current-session)))))

(defn- show-day [{:keys [name sessions]}]
  ^{:key name}
  [:tr
   [:td [:a {:href (str "/#activities/" name)} name]]
   [:td (util/hours-str (activities/weekly-time-spent sessions))]
   [:td [session-action-button name]]
   [:td (delete-button name)]])

(defn- activities-list []
  (let [activities (subscribe [:activities])]
    (fn []
      [:table.table
       [:thead
        [:tr [:td "Activity"] [:td "Weekly Time Spent"]]]
       [:tbody
        (doall
          (for [activity (keys @activities)]
            (show-day {:name activity :sessions (@activities activity)})))]])))

(defn page []
  (let [current-session (subscribe [:current-session])]
    (fn []
      [:div.col-md-6.col-md-offset-3
       [:div.page-header
        [:h1 "Activities"]]
       (if (nil? @current-session)
         [form/form])
       [activities-list]])))

