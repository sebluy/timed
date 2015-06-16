(ns bed-time.activities.form.handlers
  (:require [re-frame.core :refer [register-handler dispatch]]
            [bed-time.activities.activities :as activities]))

(defn register []
  (register-handler
    :update-activity-form
    (fn [db [_ activity]]
      (assoc-in db [:activity-form :field] activity)))

  (register-handler
    :submit-activity-form
    (fn [db _]
      (let [activity (get-in db [:activity-form :field])]
        (if-let [error (activities/error activity)]
          (assoc-in db [:activity-form :error] error)
          (do (dispatch [:new-session activity])
              (assoc db :activity-form {:field nil :error nil})))))))

