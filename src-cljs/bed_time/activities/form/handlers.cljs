(ns bed-time.activities.form.handlers
  (:require [bed-time.activities.form.transitions :as transitions]
            [bed-time.framework.db :as db]
            [bed-time.sessions.handlers :as session-handlers]))

(defn update-field [text]
  (db/transition (transitions/update-field text)))

(defn submit [activity error pending]
  (when-not (or error pending)
    (session-handlers/start-session
      activity
     :activity-form
     (transitions/update-field nil))))
