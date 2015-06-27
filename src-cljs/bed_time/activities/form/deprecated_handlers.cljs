(ns bed-time.activities.form.handlers
  (:require [re-frame.core :refer [dispatch register-handler trim-v path]]
            [bed-time.sessions.sessions :as sessions]))

(register-handler
  :update-activity-form
  (comp trim-v (path :page :activity-form))
  (fn [activity-form [activity]]
    (assoc activity-form :field activity)))

(register-handler
  :submit-activity-form
  (fn [db [_ activity]]
    (sessions/post-update-session
      (sessions/new-session activity)
      (fn [_ session]
        (dispatch [:receive-activity-form])
        (dispatch [:receive-update-session session])))
    (assoc-in db [:page :activity-form :pending] true)))

(register-handler
  :receive-activity-form
  (fn [db [_]]
    (->> (dissoc (db :page) :activity-form)
         (assoc db :page))))

