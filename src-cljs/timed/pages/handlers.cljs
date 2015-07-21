(ns timed.pages.handlers
  (:require [timed.pages.transitions :as transitions]
            [timed.framework.db :as db]
            [timed.activities.activities :as activities]
            [cljs.core.async :as async]
            [timed.remote-handlers :as remote-handlers]
            [timed.sessions.sessions :as sessions])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn refresh-activities []
  (go
    (let [raw (async/<! (remote-handlers/get-activities))
          sorted (activities/coerce-activities-to-sorted raw)
          current-session (sessions/current sorted)
          tick (db/query-once [:tick])]
      (db/transition
        (comp
          (cond
            (and current-session (nil? tick)) transitions/start-tick
            (and (nil? current-session) tick) transitions/stop-tick
            :else identity)
          (transitions/update-activities sorted))))))

(defn get-activities []
  (db/transition (transitions/update-activities :pending))
  (go
    (let [raw (async/<! (remote-handlers/get-activities))
          sorted (activities/coerce-activities-to-sorted raw)
          current-session (sessions/current sorted)]
      (db/transition
        (comp
          (if current-session
            transitions/start-tick
            identity)
          (transitions/update-activities sorted))))))

(defn go-offline []
  (db/transition transitions/go-offline))

(defn go-online []
  (let [actions (db/query-once [:offline-actions])]
    (if (seq actions)
      (remote-handlers/post-actions actions)))
  (db/transition transitions/go-online))
