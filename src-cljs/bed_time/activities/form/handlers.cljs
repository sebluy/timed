(ns bed-time.activities.form.handlers
  (:require [re-frame.core :refer [register-handler trim-v path]]
            [bed-time.activities.activities :as activities]))

(register-handler
  :update-activity-form
  (comp trim-v (path :page :activity-form))
  (fn [activity-form [activity]]
    (assoc activity-form :field activity :error (activities/error activity))))

