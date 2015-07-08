(ns bed-time.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.activities.subs]
            [bed-time.activities.form.subs]
            [bed-time.sessions.subs]
            [bed-time.sessions.form.subs]
            [bed-time.activities.activities :as activities]
            [bed-time.sessions.sessions :as sessions]))

(defn- current-session []
  (sessions/current (db/query-db [:activities])))

(defn- aggregates [path]
  (-> (db/query-db [:activities])
      (activities/build-aggregates)
      (activities/add-week-total)
      (get-in path)))

(defn- pending-session []
  (sessions/pending (db/query [:activities])))

(defn- activity-form-visible? []
  (let [pending-session (pending-session)
        {:keys [source action]} (:pending pending-session)]
    (or (= source :activity-form)
        (and (nil? (current-session)) (not= :start action)))))

(db/register-derived-query [:aggregates] aggregates)
(db/register-derived-query [:current-session] current-session)
(db/register-derived-query [:pending-session] pending-session)
(db/register-derived-query [:activity-form-visible?] activity-form-visible?)
