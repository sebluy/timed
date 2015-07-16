(ns timed.pages.subs
  (:require [timed.framework.db :as db]
            [timed.activities.subs]
            [timed.activities.form.subs]
            [timed.sessions.subs]
            [timed.sessions.form.subs]
            [timed.activities.activities :as activities]
            [timed.sessions.sessions :as sessions])
  (:require-macros [timed.macros :refer [with-subs]]))

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
