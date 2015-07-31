(ns timed.sessions.handlers
  (:require [timed.db :as db]
            [timed.sessions.transitions :as session-transitions]
            [timed.remote-handlers :as remote-handlers]
            [timed.pages.transitions :as page-transitions]
            [timed.sessions.sessions :as sessions]))

(defn add-session
  ([session] (add-session session identity))
  ([session transition]
   (db/transition
     (comp
       transition
       (if (sessions/current? session)
         page-transitions/start-tick
         identity)
       (session-transitions/add-session session)))
   (remote-handlers/add-session session)))

(defn update-session
  ([old-session new-session]
   (update-session old-session new-session identity))
  ([old-session new-session transition]
   (db/transition
     (comp
       transition
       (condp = (map sessions/current? [old-session new-session])
         [true false]
         page-transitions/stop-tick
         [false true]
         page-transitions/start-tick
         identity)
       (session-transitions/update-session old-session new-session)))
   (remote-handlers/update-session old-session new-session)))

(defn start-session
  ([activity] (start-session activity identity))
  ([activity transition]
   (let [session {:activity activity
                  :start    (js/Date.)
                  :finish   nil}]
     (db/transition
       (comp
         transition
         page-transitions/start-tick
         (session-transitions/add-session session)))
     (remote-handlers/add-session session))))

(defn finish-session [session]
  (let [finished (assoc session :finish (js/Date.))]
    (db/transition
      (comp
        page-transitions/stop-tick
        (session-transitions/update-session session finished)))
    (remote-handlers/update-session session finished)))

(defn delete-session [session]
  (db/transition
    (comp
      (if (sessions/current? session)
        page-transitions/stop-tick
        identity)
      (session-transitions/delete-session session)))
  (remote-handlers/delete-session session))

