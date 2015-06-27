(ns bed-time.sessions.list
  (:require [bed-time.sessions.sessions :as session]
            [bed-time.sessions.form.components :as form]
            [bed-time.util :as util]
            [bed-time.framework.subscriptions :refer [subscribe]]
            [re-frame.core :refer [dispatch]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn- delete-activity-button [activity]
  [:input.btn.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(dispatch [:delete-activity activity])}])

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

(defn- show-session []
  (let [session-under-edit (subscribe [:page :session-form :old-session])]
    (fn [{:keys [start finish] :as session}]
      [:tr
       (if (= session @session-under-edit)
         {:class "active"})
       [:td (.toLocaleString start)]
       [:td (if-not (session/current? session)
              (.toLocaleString finish)
              "Unfinished")]
       [:td (if-not (session/current? session)
              (util/time-str (session/time-spent session)))]
       [:td (edit-session-button session)]
       [:td (delete-button session)]])))

(defn- session-list [activity]
  (let [sessions (subscribe [:activities activity])]
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
  (let [edit-form (subscribe [:page :session-form])]
    (fn []
      (if @edit-form
        [form/edit-form]))))

(defn page [{:keys [page]}]
  (let [activity (subscribe [:page :route-params :activity])]
    (fn []
      [:div.col-md-8.col-md-offset-2
       [page-header @activity]
       [edit-form-slot]
       [session-list @activity]])))

