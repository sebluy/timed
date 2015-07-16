(ns timed.activities.form.subs
  (:require [timed.framework.db :as db]
            [timed.activities.activities :as activities])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn- error []
  (with-subs
    [field [:page :activity-form :field]]
    (fn []
      (activities/error @field))))

(defn- activity-form-source? [session]
  (= (get-in session [:pending :source]) :activity-form))

(defn- action-start? [session]
  (= (get-in session [:pending :action]) :start))

(defn- status []
  (with-subs
    [pending-sessions [:pending-sessions]
     current-session [:current-session]
     error [:page :activity-form :error]]
    (fn []
      (cond
        (or (nil? @pending-sessions) (= @pending-sessions :pending))
        :hidden
        (some activity-form-source? @pending-sessions)
        :pending
        (some action-start? @pending-sessions) ; hide on start from other source
        :hidden
        @current-session
        :hidden
        @error
        :error
        :else
        :valid))))

(db/register-derived-query [:page :activity-form :status] status)
(db/register-derived-query [:page :activity-form :error] error)
