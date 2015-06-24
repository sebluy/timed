(ns bed-time.sessions.list
  (:require [bed-time.sessions.sessions :as session]
            [bed-time.sessions.form.components :as form]
            [bed-time.util :as util]
            [re-frame.core :refer [dispatch subscribe]])
  (:require-macros [reagent.ratom :refer [reaction]]))

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

(defn- show-session [activity [start finish :as session] session-under-edit]
  (let [session-map {:activity activity :start start :finish finish}]
    ^{:key (.getTime start)}
    [:tr
     (if (= session-map @session-under-edit)
       {:class "active"})
     [:td (.toLocaleString start)]
     [:td (if (session/valid? session)
            (.toLocaleString finish))]
     [:td (if (session/valid? session)
            (util/time-str (session/time-spent session)))]
     [:td (edit-session-button session-map)]
     [:td (delete-button session-map)]]))

(defn- session-list [activity-reaction sessions-reaction session-under-edit]
  [:table.table
   [:thead
    [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent"]]]
   [:tbody
    (doall
      (for [session @sessions-reaction]
        (show-session @activity-reaction session session-under-edit)))]])

(defn- page-header [activity]
  (let [current-activity @activity]
    [:div.page-header
     [:h1 current-activity
      [:p.pull-right.btn-toolbar
       [start-session-button current-activity]
       [new-session-form-button current-activity]
       [delete-activity-button current-activity]]]]))

(defn edit-form-slot [form]
  (let [visible (reaction (boolean @form))]
    (fn []
      (if @visible
        [form/edit-form form]))))

(defn page [{:keys [page activities]}]
  (let [activity (reaction (get-in @page [:route-params :activity]))
        sessions (reaction (get @activities @activity))
        form (reaction (get @page :session-form))
        session-under-edit (reaction (get @form :old-session))]
    (fn []
      [:div.col-md-8.col-md-offset-2
       [page-header activity]
       [edit-form-slot form]
       [session-list activity sessions session-under-edit]])))

