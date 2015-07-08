(ns bed-time.pages.activities
  (:require [bed-time.activities.form.components :as form-components]
            [bed-time.activities.components :as components]
            [bed-time.framework.db :as db]))

(defn page []
  [:div
   [:div.page-header
    [:h1 "Activities"]]
   [components/activities-table]])

