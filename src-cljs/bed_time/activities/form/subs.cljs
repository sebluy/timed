(ns bed-time.activities.form.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.activities.activities :as activities]))

(defn- error []
  (activities/error (db/query [:page :activity-form :field])))

(db/register-virtual-sub [:page :activity-form :error] error)
