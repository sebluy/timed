(ns bed-time.sessions.form.handlers
  (:require [bed-time.sessions.sessions :as sessions]
            [bed-time.framework.db :as db]
            [bed-time.util :as util]
            [bed-time.sessions.form.transitions :as form-transitions]
            [bed-time.sessions.handlers :as session-handlers]))

(defn open [session]
  (db/transition (form-transitions/open session)))

(defn close []
  (db/transition form-transitions/close))

(defn update-field [key text]
  (db/transition
    (form-transitions/update-field key text)))

(defn submit []
  (let [{:keys [activity new old-session inputs]}
        (db/query-once [:page :session-form])
        new-session
        {:activity activity
         :start    (util/str->date (inputs :start))
         :finish   (util/str->date (inputs :finish))}]
    (when (sessions/valid? new-session)
      (if new
        (session-handlers/add-session
          new-session
          :session-form
          form-transitions/close)
        (session-handlers/update-session
          old-session
          new-session
          :session-form
          form-transitions/close)))))

