(ns timed.sessions.form.handlers
  (:require [timed.sessions.sessions :as sessions]
            [timed.db :as db]
            [timed.util :as util]
            [timed.sessions.form.transitions :as form-transitions]
            [timed.sessions.handlers :as session-handlers]))

(defn open [session]
  (db/transition (form-transitions/open session)))

(defn close []
  (db/transition form-transitions/close))

(defn update-field [key text]
  (db/transition
    (form-transitions/update-field key text)))

(defn submit []
  (let [{:keys [activity new old-session inputs]}
        (db/query [:page :session-form])
        new-session
        {:activity activity
         :start    (util/str->date (inputs :start))
         :finish   (util/str->date (inputs :finish))}]
    (when (sessions/valid? new-session)
      (if new
        (session-handlers/add-session
          new-session
          form-transitions/close)
        (session-handlers/update-session
          old-session
          new-session
          form-transitions/close)))))

