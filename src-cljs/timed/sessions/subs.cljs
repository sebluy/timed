(ns timed.sessions.subs
  (:require [sigsub.core :as sigsub :include-macros :true]))

(defn- sessions []
  (sigsub/with-signals
    [activity [:page :route-params :activity]
     activities [:activities]]
     (fn []
       (if (= @activities :pending)
         :pending
         (get-in @activities [@activity :sessions])))))

(defn- action-button-status [[activity _]]
  (sigsub/with-signals
    [current-session [:current-session]]
    (fn []
      (let [{current-activity :activity} @current-session]
        (cond
          (and current-activity (not= activity current-activity))
          :hidden
          (= activity current-activity)
          :finish
          :else
          :start)))))

(sigsub/register-signal-skeleton [:page :sessions] sessions)
(sigsub/register-signal-skeleton [:action-button-status] action-button-status)
