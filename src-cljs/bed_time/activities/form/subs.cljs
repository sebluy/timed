(ns bed-time.activities.form.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.activities.activities :as activities])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn- error []
  (with-subs
    [field [:page :activity-form :field]]
    (fn []
      (activities/error @field))))

(defn- pending []
  (with-subs
    [pending-session [:pending-session]]
    (fn []
      (= (get-in @pending-session [:pending :source])
         :activity-form))))

(db/register-derived-query [:page :activity-form :error] error)
(db/register-derived-query [:page :activity-form :pending] pending)
