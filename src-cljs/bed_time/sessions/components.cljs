(ns bed-time.sessions.components
  (:require [bed-time.components :as components]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.sessions.handlers :as session-handlers]
            [bed-time.sessions.form.handlers :as form-handlers]
            [bed-time.sessions.form.transitions :as form-transitions]
            [bed-time.util :as util]
            [bed-time.framework.db :as db])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn- start-button [activity class source]
  (with-subs [pending [:pending-session]]
    (fn []
      (cond
        (= @pending nil)
        [:input.btn
         {:type     "button"
          :class    (str class " btn-success")
          :value    "Start"
          :on-click #(session-handlers/start-session activity source identity)}]
        (and (= (:activity @pending) activity)
             (= (get-in @pending [:pending :source]) source))
        [components/pending-button class]))))

(defn- finish-button [source inner class]
  (with-subs [pending [:pending-session]]
    (fn [session]
      (cond
        (= @pending nil)
        [:button.btn
         {:type     "button"
          :class    (str class " btn-danger")
          :on-click #(session-handlers/finish-session session source)}
         inner]
        (and (= (:activity @pending) (:activity session))
             (= (get-in @pending [:pending :source]) source))
        [components/pending-button class]))))

(defn action-button [activity class source]
  (with-subs [current-session [:current-session]]
    (fn []
      (cond (nil? @current-session)
            [start-button activity class source]
            (= activity (@current-session :activity))
            [(finish-button source "Finish" class) @current-session]))))

(defn new-button [activity]
  [:input.btn.btn-primary
   {:type     "button"
    :value    "New"
    :on-click #(form-handlers/open {:activity activity :new true})}])

(defn- edit-button [session]
  [:input.btn.btn-sm.btn-warning
   {:type     "button"
    :value    "Edit"
    :on-click #(form-handlers/open session)}])

(defn- delete-button [session]
  (with-subs [pending [:pending-session]]
    (fn []
      (if (and (= (get-in @pending [:pending :action]) :delete)
               (= (@pending :start) (session :start)))
        [components/pending-button "btn-sm"]
        [:input.btn.btn-sm.btn-danger
         {:type     "button"
          :value    "Delete"
          :on-click #(session-handlers/delete-session session)}]))))

(defn- show-session []
  (with-subs [session-under-edit [:page :session-form :old-session]]
    (fn [{:keys [start finish] :as session}]
      [:tr
       (if (= session @session-under-edit)
         {:class "active"})
       [:td (sessions/string :start start)]
       [:td (sessions/string :finish finish)]
       [:td (util/time-str (sessions/time-spent session))]
       [:td
        [:p.btn-toolbar
         (edit-button session)
         [delete-button session]]]])))

(defn session-list [activity]
  (with-subs [sessions [:activities activity]]
    (fn []
      [:table.table
       [:thead
        [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent"]]]
       [:tbody
        (doall
          (for [[start session] @sessions]
            ^{:key start}
            [show-session session]))]])))

