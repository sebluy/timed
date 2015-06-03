(ns bed-time.day-list
  (:require [bed-time.days :as days]
            [bed-time.util :as util]
            [bed-time.form :as form]
            [bed-time.page :as page]
            [bed-time.state :as state]))

(defn delete-day-button [day]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Delete!"
    :on-click #(days/delete-day day)}])

(defn edit-day-button [day]
  [:input.btn.btn-sm.btn-warning
   {:type     "button"
    :value    "Edit!"
    :on-click #(form/inject-day day)}])

(defn show-day [[bed-time wake-up-time :as day]]
  ^{:key (.getTime bed-time)}
  [:tr
   [:td (.toLocaleString bed-time)]
   [:td (if (days/valid? day) (.toLocaleString wake-up-time))]
   [:td (if (days/valid? day) (util/hours-str (days/time-slept day) 2))]
   [:td (edit-day-button day)]
   [:td (delete-day-button day)]])

(defn day-list []
  [:table.table
   [:thead
    [:tr [:td "Bed Time"] [:td "Wake Up Time"] [:td "Time Slept (Hours)"]]]
   [:tbody
    (for [day @days/days]
      (show-day day))]])

(defn page []
  (page/page
    [:div
     (if (@state/state :update-form)
       [form/update-form])
     [day-list]]))
