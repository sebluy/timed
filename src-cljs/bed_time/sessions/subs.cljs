(ns bed-time.sessions.subs
  (:require [bed-time.framework.db :as db])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn- sessions []
  (with-subs
    [activity [:page :route-params :activity]
     activities [:activities]]
     (fn []
       (get @activities @activity))))

(defn- action-button-status [[activity source]]
  (with-subs
    [pending-session [:pending-session]
     current-session [:current-session]]
    (fn []
      (let [{pending-activity :activity
             {pending-source :source pending-action :action} :pending}
            @pending-session
            {current-activity :activity} @current-session]
        (cond
          (and (= pending-activity activity)
               (= pending-source source))
          :pending
          (or (and current-activity (not= activity current-activity))
              (= pending-action :start)
              (= pending-action :finish))
          :hidden
          (= activity current-activity)
          :finish
          :else
          :start)))))

(defn- delete-button-status [[session]]
  (with-subs
    [pending-session [:pending-session]]
    (fn []
      (let [{pending-start :start {pending-action :action} :pending}
            @pending-session]
        (if (and (= pending-start (session :start))
                 (= pending-action :delete))
          :pending
          :visible)))))

(db/register-derived-query [:page :sessions] sessions)
(db/register-derived-query [:action-button-status] action-button-status)
(db/register-derived-query [:delete-button-status] delete-button-status)
