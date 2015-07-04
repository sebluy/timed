(ns bed-time.sessions.components
  (:require [bed-time.sessions.sessions :as sessions]
            [bed-time.sessions.handlers :as session-handlers]
            [bed-time.sessions.form.transitions :as form-transitions]
            [bed-time.util :as util]
            [bed-time.framework.db :as db]
            [bed-time.activities.handlers :as activity-handlers]))

(defn- pending-button [class]
  [:input.btn
   {:type  "button"
    :class (str class " btn-warning")
    :value "Pending"}])

(defn delete-activity-button [activity]
  (let [pending (db/subscribe [:pending :delete-activity])]
    (fn []
      (if @pending
        [pending-button]
        [:input.btn.btn-danger
         {:type     "button"
          :value    "Delete"
          :on-click #(activity-handlers/delete-activity activity)}]))))

(defn- start-session-button [activity class]
  (let [pending (db/subscribe [:pending :start-session])]
    (fn []
      (condp = @pending
        nil [:input.btn
             {:type     "button"
              :class    (str class " btn-success")
              :value    "Start"
              :on-click #(session-handlers/start-session activity)}]
        activity [pending-button class]
        nil))))

(defn- finish-session-button [session inner class]
  (let [pending (db/subscribe [:pending :finish-session])]
    (fn []
      (condp = (:activity @pending)
        nil [:button.btn
             {:type     "button"
              :class    (str class " btn-danger")
              :on-click #(session-handlers/finish-session session)}
             inner]
        (session :activity) [pending-button class]
        nil))))

(defn session-action-button [activity class]
  (let [current-session (db/subscribe [:current-session])]
    (fn []
      (cond (nil? @current-session)
            [start-session-button activity class]
            (= activity (@current-session :activity))
            [finish-session-button @current-session "Finish" class]))))

(defn new-session-form-button [activity]
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

(defn- delete-session-button [session]
  (let [pending (db/subscribe [:pending :delete-session])]
    (fn []
      (if (= (:start @pending) (session :start))
        [pending-button "btn-sm"]
        [:input.btn.btn-sm.btn-danger
         {:type     "button"
          :value    "Delete"
          :on-click #(session-handlers/delete-session session)}]))))

(defn- show-session []
  (let [session-under-edit (db/subscribe [:page :session-form :old-session])]
    (fn [{:keys [start finish] :as session}]
      [:tr
       (if (= session @session-under-edit)
         {:class "active"})
       [:td (sessions/string :start start)]
       [:td (sessions/string :finish finish)]
       [:td (util/time-str (sessions/time-spent session))]
       [:td
        [:p.btn-toolbar
         (edit-session-button session)
         [delete-session-button session]]]])))

(defn session-list [activity]
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

