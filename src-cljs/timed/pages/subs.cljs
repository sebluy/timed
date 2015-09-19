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
      (if @current-session
        (util/time-diff (@current-session :start) @now)
        0))))

(sigsub/register-signal-skeleton [:aggregates] aggregates)
(sigsub/register-signal-skeleton [:aggregates-base] aggregates-base)
(sigsub/register-signal-skeleton [:current-session] current-session)
(sigsub/register-signal-skeleton [:current-session-time-spent]
                                   current-session-time-spent)

