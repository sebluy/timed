(ns bed-time.activities.show
  (:require [bed-time.sessions.sessions :as session]
            [bed-time.sessions.form :as form]
            [bed-time.util :as util]
            [bed-time.state :as state]))

(defn delete-button [activity session]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(session/delete activity session)}])

(defn edit-session-button [session]
  [:input.btn.btn-sm.btn-warning
   {:type     "button"
    :value    "Edit!"
    :on-click #(form/inject-session session)}])

(defn show-session [activity [start finish :as session]]
  ^{:key (.getTime start)}
  [:tr
   [:td (.toLocaleString start)]
   [:td (if (session/valid? session)
          (.toLocaleString finish))]
   [:td (if (session/valid? session)
          (util/hours-str (session/time-spent session) 2))]
   [:td (edit-session-button session)]
   [:td (delete-button activity session)]])

(defn session-list [activity sessions]
  [:table.table
   [:thead
    [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent (Hours)"]]]
   [:tbody
    (for [session sessions]
      (show-session activity session))]])

(defn page [params]
  (let [activity-name (params :activity)]
    (fn []
      [:div.col-md-8.col-md-offset-2
       [:div.page-header [:h1 activity-name]]
       (if (@state/state :update-form)
         [form/update-form activity-name])
       [session-list activity-name (get @state/activities activity-name)]])))
