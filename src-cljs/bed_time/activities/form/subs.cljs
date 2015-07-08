(ns bed-time.activities.form.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.activities.activities :as activities]
            [bed-time.sessions.sessions :as sessions]))

(defn- error []
  (activities/error (db/query [:page :activity-form :field])))

(defn- pending []
  (= (get-in (sessions/pending (db/query [:activities])) [:pending :source])
     :activity-form))

(db/register-derived-query [:page :activity-form :error] error)
(db/register-derived-query [:page :activity-form :pending] pending)
