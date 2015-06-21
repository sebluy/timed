(ns bed-time.sessions.list
  (:require [bed-time.sessions.sessions :as session]
            [bed-time.sessions.form.components :as form]
            [bed-time.util :as util]
            [re-frame.core :refer [dispatch subscribe]]))

(defn- delete-activity-button [activity]
  [:input.btn.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click (fn []
                (dispatch [:delete-activity activity]))}])

(defn- start-session-button [activity]
  [:input.btn.btn-success
   {:type     "button"
    :value    "Start"
    :on-click #(dispatch [:start-session activity])}])

(defn- new-session-form-button [activity]
  [:input.btn.btn-primary
   {:type     "button"
    :value    "New Session Form"
    :on-click #(dispatch [:open-session-form {:activity activity :new true}])}])

(defn- edit-session-button [session]
  [:input.btn.btn-sm.btn-warning
   {:type     "button"
    :value    "Edit!"
    :on-click #(dispatch [:open-session-form session])}])

(defn- delete-button [session]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(dispatch [:delete-session session])}])

(defn- show-session [activity [start finish :as session]]
  (let [session-map {:activity activity :start start :finish finish}]
    ^{:key (.getTime start)}
    [:tr
     [:td (.toLocaleString start)]
     [:td (if (session/valid? session)
            (.toLocaleString finish))]
     [:td (if (session/valid? session)
            (util/time-str (session/time-spent session)))]
     [:td (edit-session-button session-map)]
     [:td (delete-button session-map)]]))

(defn- session-list [activity]
  (let [sessions (subscribe [:activity activity])]
    (fn []
      [:table.table
       [:thead
        [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent"]]]
       [:tbody
        (for [session @sessions]
          (show-session activity session))]])))

(defn- page-header [activity]
  [:div.page-header
   [:h1 activity
    [:p.pull-right.btn-toolbar
     [start-session-button activity]
     [new-session-form-button activity]
     [delete-activity-button activity]]]])

(defn page []
  (let [page (subscribe [:page])
        session-form (subscribe [:session-form])]
    (fn []
      (let [activity (get-in @page [:route-params :activity])]
        [:div.col-md-8.col-md-offset-2
         [page-header activity]
         (if @session-form
           [form/edit-form session-form])
         [session-list activity]]))))

