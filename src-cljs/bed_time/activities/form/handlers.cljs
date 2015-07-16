(ns bed-time.activities.form.handlers
  (:require [bed-time.activities.form.transitions :as transitions]
            [bed-time.framework.db :as db]
            [bed-time.sessions.handlers :as session-handlers]))

(defn update-field [text]
  (db/transition (transitions/update-field text)))

(defn submit []
  (let [activity (db/query-once [:page :activity-form :field])
        status (db/query-once [:page :activity-form :status])]
    (when (= status :valid)
      (session-handlers/start-session
        activity
        :activity-form
        (transitions/update-field nil)))))
