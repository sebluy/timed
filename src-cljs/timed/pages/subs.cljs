(ns timed.pages.subs
  (:require [timed.activities.subs]
            [timed.activities.form.subs]
            [timed.sessions.subs]
            [timed.sessions.form.subs]
            [timed.activities.activities :as activities]
            [timed.sessions.sessions :as sessions]
            [timed.util :as util]
            [sigsub.core :as sigsub :include-macros :true]))

(defn- current-session []
  (sigsub/with-signals
    [activities [:activities]]
    (fn []
      (println "Running current-session")
      (sessions/current @activities))))

(defn- aggregates-base []
  (sigsub/with-signals
    [activities [:activities]]
    (fn []
      (if (= @activities :pending)
        :pending
        (-> @activities
            (activities/build-aggregates)
            (activities/add-week-total))))))

(defn- aggregates [path]
  (sigsub/with-signals
    [aggregates [:aggregates-base]]
    (fn []
      (if (= @aggregates :pending)
        :pending
        (get-in @aggregates path)))))

(defn- current-session-time-spent []
  (sigsub/with-signals
    [current-session [:current-session]
     now [:tick :now]]
    (fn []
      (println "running time-spent")
      (if @current-session
        (util/time-diff (@current-session :start) @now)
        0))))

(sigsub/register-derived-signal-fn [:aggregates] aggregates)
(sigsub/register-derived-signal-fn [:aggregates-base] aggregates-base)
(sigsub/register-derived-signal-fn [:current-session] current-session)
(sigsub/register-derived-signal-fn [:current-session-time-spent]
                                   current-session-time-spent)
