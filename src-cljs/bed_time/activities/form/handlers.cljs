(ns bed-time.activities.form.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [bed-time.middleware :refer [remove-v]]))

(register-handler
  :update-activity-form
  (comp trim-v (path :page :activity-form))
  (fn [activity-form [activity]]
    (assoc activity-form :field activity)))

