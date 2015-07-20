(ns timed.pages.activities
  (:require [timed.activities.components :as components]
            [timed.pages.handlers :as handlers]))

(defn refresh-button []
  [:input.btn.btn-primary
   {:type     "button"
    :value    "Refresh"
    :on-click #(handlers/refresh-activities)}])

(defn page []
  [:div
   [:div.page-header
    [:h1 "Activities"
     [:p.pull-right.btn-toolbar
      [refresh-button]]]]
   [components/activities-table]])

