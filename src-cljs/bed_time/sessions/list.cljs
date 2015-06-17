(ns bed-time.sessions.list
  (:require [bed-time.sessions.sessions :as session]
            [bed-time.sessions.form :as form]
            [bed-time.util :as util]
            [re-frame.core :refer [dispatch subscribe]]))

(defn- delete-button [session]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(dispatch [:delete-session session])}])

(defn- edit-session-button [session]
  [:input.btn.btn-sm.btn-warning
   {:type     "button"
    :value    "Edit!"
    :on-click #(dispatch [:edit-session session])}])

(defn- new-session-button [activity]
  [:input.btn.btn-large.btn-primary.pull-right
   {:type     "button"
    :value    "New Day!"
    :on-click #(dispatch
                [:edit-session
                 {:activity activity :new true}])}])

(defn- show-session [activity [start finish :as session]]
  (let [session-map {:activity activity :start start :finish finish}]
    ^{:key (.getTime start)}
    [:tr
     [:td (.toLocaleString start)]
     [:td (if (session/valid? session)
            (.toLocaleString finish))]
     [:td (if (session/valid? session)
            (util/hours-str (session/time-spent session)))]
     [:td (edit-session-button session-map)]
     [:td (delete-button session-map)]]))

(defn- session-list [activity]
  (let [sessions (subscribe [:activity activity])]
    (fn []
      [:table.table
       [:thead
        [:tr [:td "Start"] [:td "Finish"] [:td "Time Spent (Hours)"]]]
       [:tbody
        (for [session @sessions]
          (show-session activity session))]])))

(defn page []
  (let [page (subscribe [:page])
        edit-session-form (subscribe [:edit-session-form])]
    (fn []
      (let [activity (get-in @page [:route-params :activity])]
        [:div.col-md-8.col-md-offset-2
         [:div.page-header [:h1 activity (new-session-button activity)]]
         (if @edit-session-form
           [form/edit-form edit-session-form])
         [session-list activity]]))))

