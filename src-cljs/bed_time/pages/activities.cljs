(ns bed-time.pages.activities
  (:require [bed-time.activities.components :as components]))

(defn page []
  [:div
   [:div.page-header
    [:h1 "Activities"]]
   [components/activities-table]])

