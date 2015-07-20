(ns timed.sessions.components
  (:require [timed.sessions.sessions :as sessions]
            [timed.sessions.handlers :as session-handlers]
            [timed.sessions.form.handlers :as form-handlers]
            [timed.util :as util])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn- start-button [activity class]
  [:input.btn
   {:type     "button"
    :class    (str class " btn-success")
    :value    "Start"
    :on-click #(session-handlers/start-session activity identity)}])

(defn- finish-button [class]
  (with-subs
    [current-session [:current-session]]
    (fn []
      [:input.btn
       {:type     "button"
        :class    (str class " btn-danger")
        :value    "Finish"
        :on-click #(session-handlers/finish-session @current-session)}])))

(defn action-button [activity class]
  (with-subs
    [current-session [:current-session]
     action-button-status [:action-button-status activity]]
    (fn []
      (condp = @action-button-status
        :start [start-button activity class]
        :finish [finish-button class]
        :hidden nil))))

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
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete"
    :on-click #(session-handlers/delete-session session)}])

(defn- show-session []
  (with-subs
    [session-under-edit [:page :session-form :old-session]
     current-session-time-spent [:current-session-time-spent]]
    (fn [{:keys [start finish] :as session}]
      [:tr
       (if (= session @session-under-edit)
         {:class "active"})
       [:td (sessions/string :start start)]
       [:td (sessions/string :finish finish)]
       [:td (util/time-str (if (sessions/current? session)
                             @current-session-time-spent
                             (sessions/time-spent session)))]
       [:td
        [:p.btn-toolbar
         (edit-button session)
         [delete-button session]]]])))

(defn- session-list []
  (with-subs
    [sessions [:page :sessions]]
    (fn []
      [:table.table
       [:thead
        [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent"]]]
       [:tbody
        (doall
          (for [[start session] @sessions]
            ^{:key start}
            [show-session session]))]])))

(defn session-list-slot []
  (with-subs
    [sessions [:page :sessions]]
    (fn []
      (cond
        (= @sessions :pending)
        [:div.jumbotron [:h1.text-center "Pending"]]
        (seq @sessions)
        [session-list]
        :else
        [:div.jumbotron [:h1.text-center "No Sessions"]]))))
