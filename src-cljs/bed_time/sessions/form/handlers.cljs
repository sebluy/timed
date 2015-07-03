(ns bed-time.sessions.form.handlers
  (:require [bed-time.sessions.sessions :as sessions]
            [bed-time.framework.db :as db]
            [bed-time.util :as util]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.sessions.handlers :as handlers]
            [bed-time.sessions.form.transitions :as transitions]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :as async]))

(defn submit []
  (let [{:keys [activity new old-session fields]}
        (db/query [:page :session-form])
        new-session
        {:activity activity
         :start    (util/str->date (fields :start))
         :finish   (util/str->date (fields :finish))}]
    (when (sessions/valid? new-session)
      (async/go
        (let [transition (async/<!
                           (if new
                             (handlers/update-session-transition-chan
                               (assoc new-session :new true))
                             (handlers/swap-session-transition-chan
                               old-session
                               new-session)))]
          (db/transition (comp transitions/close transition)))))))

