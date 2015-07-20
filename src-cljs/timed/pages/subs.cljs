(ns timed.pages.subs
  (:require [timed.framework.db :as db]
            [timed.activities.subs]
            [timed.activities.form.subs]
            [timed.sessions.subs]
            [timed.sessions.form.subs]
            [timed.activities.activities :as activities]
            [timed.sessions.sessions :as sessions]
            [timed.util :as util])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn- current-session []
  (with-subs
    [activities [:activities]]
    (fn []
      (sessions/current @activities))))

(defn- aggregates-base []
  (with-subs
    [activities [:activities]]
    (fn []
      (if (= @activities :pending)
        :pending
        (-> @activities
            (activities/build-aggregates)
            (activities/add-week-total))))))

(defn- aggregates [path]
  (with-subs
    [aggregates [:aggregates-base]]
    (fn []
      (if (= @aggregates :pending)
        :pending
        (get-in @aggregates path)))))

(defn- current-session-time-spent []
  (with-subs
    [current-session [:current-session]
     now [:tick :now]]
    (fn []
      (if @current-session
        (util/time-diff (@current-session :start) @now)
        0))))

(db/register-derived-query [:aggregates] aggregates)
(db/register-derived-query [:aggregates-base] aggregates-base)
(db/register-derived-query [:current-session] current-session)
(db/register-derived-query [:current-session-time-spent]
                           current-session-time-spent)
