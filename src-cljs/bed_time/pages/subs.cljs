(ns bed-time.pages.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.activities.subs]
            [bed-time.activities.form.subs]
            [bed-time.sessions.subs]
            [bed-time.sessions.form.subs]
            [bed-time.activities.activities :as activities]
            [bed-time.sessions.sessions :as sessions])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn- current-session []
  (with-subs
    [activities [:activities]]
    (fn []
      (sessions/current @activities))))

(defn- aggregates [path]
  (with-subs
    [activities [:activities]]
    (fn []
      (-> @activities
          (activities/build-aggregates)
          (activities/add-week-total)
          (get-in path)))))

(defn- pending-sessions []
  (with-subs
    [activities [:activities]]
    (fn []
      (sessions/pending @activities))))

(db/register-derived-query [:aggregates] aggregates)
(db/register-derived-query [:current-session] current-session)
(db/register-derived-query [:pending-sessions] pending-sessions)
(db/register-derived-query [:activity-form-status] activity-form-status)
