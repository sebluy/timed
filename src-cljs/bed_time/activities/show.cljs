(ns bed-time.activities.show
  (:require [bed-time.activities.core :as core]
            [bed-time.activities.session :as session]
            [bed-time.util :as util]))

(defn delete-button [activity session]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(session/delete activity session)}])

(defn show-session [activity [start finish :as session]]
  ^{:key (.getTime start)}
  [:tr
   [:td (.toLocaleString start)]
   [:td (if (session/valid? session)
          (.toLocaleString finish))]
   [:td (if (session/valid? session)
          (util/hours-str (session/time-spent session) 2))]
   [:td (delete-button activity session)]])

(defn session-list [activity sessions]
  [:table.table
   [:thead
    [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent (Hours)"]]]
   [:tbody
    (for [session sessions]
      (show-session activity session))]])

(defn page [params]
  (fn []
    [:div.col-md-8.col-md-offset-2
     [session-list (params :activity) (@core/activities (params :activity))]]))
