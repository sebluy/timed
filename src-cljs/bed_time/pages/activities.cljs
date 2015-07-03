(ns bed-time.pages.activities
  (:require [bed-time.activities.form.components :as form-components]
            [bed-time.activities.components :as components]
            [bed-time.framework.db :as db]))

(defn- form-slot []
  (let [current-session (db/subscribe [:current-session])]
    (fn []
      (if (not @current-session)
        [form-components/form]))))

(defn page []
  [:div
   [:div.page-header
    [:h1 "Activities"]]
   [form-slot]
   [components/activities-table]])

