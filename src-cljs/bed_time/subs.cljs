(ns bed-time.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.activities.form.subs]
            [bed-time.sessions.subs]
            [bed-time.sessions.form.subs]
            [bed-time.activities.activities :as activities]
            [bed-time.sessions.sessions :as sessions]))

(defn- current-session []
  (sessions/current (db/query [:activities])))

(defn- aggregates [path]
  (-> (db/query [:activities])
      (activities/build-aggregates)
      (activities/add-week-total)
      (get-in path)))

(db/register-virtual-sub [:aggregates] aggregates)
(db/register-virtual-sub [:current-session] current-session)


