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

(defn- current-session-time-spent []
  (with-subs
    [current-session [:current-session]
     now [:tick :now]]
    (fn []
      (if @current-session
        (util/time-diff (@current-session :start) @now)
        0))))

(defn- navbar-finish-status []
  (with-subs
    [pending-sessions [:pending-sessions]]
    (fn []
      (cond
        (or (nil? @pending-sessions) (= @pending-sessions :pending))
        :hidden
        (some (fn [session] (= (get-in session [:pending :source]) :navbar))
              @pending-sessions)
        :pending
        (some (fn [session] (= (get-in session [:pending :action]) :finish))
              @pending-sessions)
        :hidden
        :else
        :visible))))

(db/register-derived-query [:navbar-finish-status] navbar-finish-status)
(db/register-derived-query [:aggregates] aggregates)
(db/register-derived-query [:current-session] current-session)
(db/register-derived-query [:current-session-time-spent]
                           current-session-time-spent)
(db/register-derived-query [:pending-sessions] pending-sessions)
