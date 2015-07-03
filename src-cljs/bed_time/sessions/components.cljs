(ns bed-time.sessions.components
  (:require [bed-time.sessions.sessions :as session]
            [bed-time.sessions.form.components :as form]
            [bed-time.activities.handlers :as activity-handlers]
            [bed-time.sessions.handlers :as session-handlers]
            [bed-time.sessions.form.transitions :as form-transitions]
            [bed-time.util :as util]
            [bed-time.framework.db :as db]))

(defn- delete-activity-button [activity]
  [:input.btn.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(activity-handlers/delete-activity activity)}])

(defn- start-session-button
  ([activity] (start-session-button activity nil))
  ([activity class]
   (let [pending (db/subscribe [:pending :start-session])]
     (fn []
       [:input.btn
        (merge {:type "button"}
               (if @pending
                 {:class (str class " btn-warning")
                  :value "Pending"}
                 {:class    (str class " btn-success")
                  :value    "Start"
                  :on-click #(session-handlers/start-session activity)}))]))))

(defn finish-session-button
  ([session] (finish-session-button session nil))
  ([session class]
   [:input.btn.btn-danger
    {:type     "button"
     :class    class
     :value    "Finish"
     :on-click #(session-handlers/finish-session session)}]))

(defn session-action-button
  ([activity] (session-action-button activity nil))
  ([activity class]
   (let [current-session (db/subscribe [:current-session])]
     (fn []
       (cond (nil? @current-session)
             (start-session-button activity class)
             (= activity (@current-session :activity))
             (finish-session-button @current-session class))))))

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

(defn- delete-session-button [session]
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
       [:td (delete-session-button session)]])))

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

