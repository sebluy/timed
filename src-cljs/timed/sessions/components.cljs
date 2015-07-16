(ns timed.sessions.components
  (:require [timed.pages.components :as page-components]
            [timed.sessions.sessions :as sessions]
            [timed.sessions.handlers :as session-handlers]
            [timed.sessions.form.handlers :as form-handlers]
            [timed.util :as util])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn- start-button [activity class source]
  [:input.btn
   {:type     "button"
    :class    (str class " btn-success")
    :value    "Start"
    :on-click #(session-handlers/start-session
                activity source identity)}])

(defn- finish-button [class source]
  (with-subs
    [current-session [:current-session]]
    (fn []
      [:input.btn
       {:type     "button"
        :class    (str class " btn-danger")
        :value    "Finish"
        :on-click #(session-handlers/finish-session
                    @current-session source)}])))

(defn action-button [activity class source]
  (with-subs
    [current-session [:current-session]
     action-button-status [:action-button-status activity source]]
    (fn []
      (condp = @action-button-status
        :start [start-button activity class source]
        :finish [finish-button class source]
        :pending [page-components/pending-button class]
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
  (if-not (= (get-in session [:pending :action]) :delete)
    [:input.btn.btn-sm.btn-danger
     {:type     "button"
      :value    "Delete"
      :on-click #(session-handlers/delete-session session)}]
    [page-components/pending-button "btn-sm"]))

(defn- show-session []
  (with-subs
    [session-under-edit [:page :session-form :old-session]]
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

(defn session-list []
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
