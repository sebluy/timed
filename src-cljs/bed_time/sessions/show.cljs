(ns bed-time.sessions.list
  (:require [bed-time.sessions.sessions :as session]
            [bed-time.sessions.form :as form]
            [bed-time.util :as util]
            [re-frame.core :as re-frame])
  (:require-macros [reagent.ratom :as reaction]))

#_(defn delete-button [activity session]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(session/delete activity session)}])

#_(defn edit-session-button [session]
  [:input.btn.btn-sm.btn-warning
   {:type     "button"
    :value    "Edit!"
    :on-click #(form/inject-session session)}])

#_(defn new-session-button []
  [:input.btn.btn-large.btn-primary.pull-right
   {:type     "button"
    :value    "New Day!"
    :on-click form/new-session}])

#_(defn show-session [activity [start finish :as session]]
  ^{:key (.getTime start)}
  [:tr
   [:td (.toLocaleString start)]
   [:td (if (session/valid? session)
          (.toLocaleString finish))]
   [:td (if (session/valid? session)
          (util/hours-str (session/time-spent session)))]
   [:td (edit-session-button session)]
   [:td (delete-button activity session)]])

#_(defn session-list [activity sessions]
  [:table.table
   [:thead
    [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent (Hours)"]]]
   [:tbody
    (for [session sessions]
      (show-session activity session))]])



(defn page [params])

#_(defn page [params]
  (let [activity (params :activity)
        sessions (re-frame/subscribe [:activity activity])]
    (fn []
      [:div.col-md-8.col-md-offset-2
       [:div.page-header [:h1 activity (new-session-button)]]
;       (if (@state/state :update-form)
;         [form/update-form activity])
       [session-list activity @sessions]])))
