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

(defn- pending-this-action-button? [activity source session]
  (and (= (:activity session) activity)
       (= (get-in session [:pending :source]) source)))

(defn- pending-action? [actions session]
  (contains? actions (get-in session [:pending :action])))

(defn- action-button-status [[activity source]]
  (with-subs
    [pending-sessions [:pending-sessions]
     current-session [:current-session]]
    (fn []
      (let [{current-activity :activity} @current-session]
        (cond
          (or (nil? @pending-sessions) (= @pending-sessions :pending))
          :hidden
          (some #(pending-this-action-button? activity source %)
                @pending-sessions)
          :pending
          (or (and current-activity (not= activity current-activity))
              (some #(pending-action? #{:start :finish} %) @pending-sessions))
          :hidden
          (= activity current-activity)
          :finish
          :else
          :start)))))

(db/register-derived-query [:page :sessions] sessions)
(db/register-derived-query [:action-button-status] action-button-status)
