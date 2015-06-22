(ns bed-time.activities.form.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [bed-time.middleware :refer [remove-v]]
            [bed-time.activities.activities :as activities]))

(register-handler
  :update-activity-form
  (comp trim-v (path :page :activity-form))
  (fn [activity-form [activity]]
    (assoc activity-form :field activity)))

(register-handler
  :submit-activity-form
  (comp remove-v (path :page))
  (fn [page]
    (let [activity (get-in page [:activity-form :field])]
      (if-let [error (activities/error activity)]
        (assoc-in page [:activity-form :error] error)
        (do (dispatch [:start-session activity])
            (dissoc page :activity-form))))))

