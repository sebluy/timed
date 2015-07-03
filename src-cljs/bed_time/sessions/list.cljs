(ns bed-time.sessions.list
  (:require [bed-time.sessions.sessions :as session]
            [bed-time.sessions.form.components :as form]
            [bed-time.activities.handlers :as activity-handlers]
            [bed-time.sessions.handlers :as session-handlers]
            [bed-time.sessions.form.transitions :as form-transitions]
            [bed-time.util :as util]
            [bed-time.framework.db :as db])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn- delete-activity-button [activity]
  [:input.btn.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(activity-handlers/delete-activity activity)}])

(defn- start-session-button [activity]
  [:input.btn.btn-success
   {:type     "button"
    :value    "Start"
    :on-click #(session-handlers/start-session activity)}])

(defn- new-session-form-button [activity]
  [:input.btn.btn-primary
   {:type     "button"
    :value    "New Session Form"
    :on-click #(db/transition
                (form-transitions/open {:activity activity :new true}))}])

(defn- edit-session-button [session]
  [:input.btn.btn-sm.btn-warning
   {:type     "button"
    :value    "Edit!"
    :on-click #(db/transition
                (form-transitions/open session))}])

(defn- delete-button [session]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(session-handlers/delete-session session)}])

(defn- show-session []
  (let [session-under-edit (db/subscribe [:page :session-form :old-session])]
    (fn [{:keys [start finish] :as session}]
      [:tr
       (if (= session @session-under-edit)
         {:class "active"})
       [:td (.toLocaleString start)]
       [:td (if-not (session/current? session)
              (.toLocaleString finish)
              "Unfinished")]
       [:td (util/time-str (session/time-spent session))]
       [:td (edit-session-button session)]
       [:td (delete-button session)]])))

(defn- session-list [activity]
  (let [sessions (db/subscribe [:activities activity])]
    (fn []
      [:table.table
       [:thead
        [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent"]]]
       [:tbody
        (doall
          (for [[start session] @sessions]
            ^{:key start}
            [show-session session]))]])))

(defn- page-header [activity]
  [:div.page-header
   [:h1 activity
    [:p.pull-right.btn-toolbar
     [start-session-button activity]
     [new-session-form-button activity]
     [delete-activity-button activity]]]])

(defn edit-form-slot []
  (let [edit-form (db/subscribe [:page :session-form])]
    (fn []
      (if @edit-form
        [form/edit-form]))))

(defn page []
  (let [activity (db/subscribe [:page :route-params :activity])]
    (fn []
      [:div.col-md-8.col-md-offset-2
       [page-header @activity]
       [edit-form-slot]
       [session-list @activity]])))

