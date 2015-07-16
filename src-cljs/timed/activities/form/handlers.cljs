(ns timed.activities.form.handlers
  (:require [timed.activities.form.transitions :as transitions]
            [timed.framework.db :as db]
            [timed.sessions.handlers :as session-handlers]))

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
