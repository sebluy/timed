(ns timed.sessions.subs
  (:require [timed.framework.db :as db])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn- sessions []
  (with-subs
    [activity [:page :route-params :activity]
     activities [:activities]]
     (fn []
       (if (= @activities :pending)
         :pending
         (get-in @activities [@activity :sessions])))))

(defn- action-button-status [[activity _]]
  (with-subs
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

(db/register-derived-query [:page :sessions] sessions)
(db/register-derived-query [:action-button-status] action-button-status)
