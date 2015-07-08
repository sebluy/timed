(ns bed-time.sessions.form.handlers
  (:require [bed-time.sessions.sessions :as sessions]
            [bed-time.framework.db :as db]
            [bed-time.util :as util]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.sessions.handlers :as handlers]
            [bed-time.sessions.form.transitions :as form-transitions]
            [cljs.core.async :as async]
            [bed-time.transitions :as transitions])
  (:require-macros [cljs.core.async.macros :as async]))

(defn open [session]
  (db/transition (form-transitions/open session)))

#_(defn submit []
  (let [{:keys [activity new old-session fields]}
        (db/query [:page :session-form])
        new-session
        {:activity activity
         :start    (util/str->date (get-in fields [:start :text]))
         :finish   (util/str->date (get-in fields [:finish :text]))}]
    (when (sessions/valid? new-session)
      (db/transition (transitions/add-pending :session-form true))
      (async/go
        (let [session-transition (async/<!
                                   (if new
                                     (handlers/update-session-transition-chan
                                       (assoc new-session :new true))
                                     (handlers/swap-session-transition-chan
                                       old-session
                                       new-session)))]
          (db/transition
            (comp
              (transitions/remove-pending :session-form)
              form-transitions/close
              session-transition)))))))

