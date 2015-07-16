(ns timed.pages.activities
  (:require [timed.activities.components :as components]))

(defn page []
  [:div
   [:div.page-header
    [:h1 "Activities"]]
   [components/activities-table]])

