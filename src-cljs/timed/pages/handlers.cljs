(ns timed.pages.handlers
  (:require [timed.pages.transitions :as transitions]
            [timed.db :as db]
            [timed.activities.activities :as activities]
            [timed.remote-handlers :as remote-handlers]
            [timed.sessions.sessions :as sessions]))

(remote-handlers/register-callback
  :refresh-activities
  (fn [raw-activities]
    (let [sorted (activities/coerce-activities-to-sorted raw-activities)
          current-session (sessions/current sorted)
          tick (db/query [:tick])]
      (db/transition
        (comp
          (cond
            (and current-session (nil? tick)) transitions/start-tick
            (and (nil? current-session) tick) transitions/stop-tick
            :else identity)
          (transitions/update-activities sorted))))))

(defn refresh-activities []
  (remote-handlers/get-activities :refresh-activities))

(remote-handlers/register-callback
  :get-activities
  (fn [raw-activities]
    (let [sorted (activities/coerce-activities-to-sorted raw-activities)
          current-session (sessions/current sorted)]
      (db/transition
        (comp
          (if current-session
            transitions/start-tick
            identity)
          (transitions/update-activities sorted))))))

(remote-handlers/register-callback :identity identity)

(defn get-activities []
  (db/transition (transitions/update-activities :pending))
  (remote-handlers/get-activities :get-activities))

(defn retry-failed-remote []
  (remote-handlers/retry-failed))

(defn cancel-failed-remote []
  (remote-handlers/cancel-failed))

