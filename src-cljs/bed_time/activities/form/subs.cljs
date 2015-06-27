(ns bed-time.activities.form.subs
  (:require [bed-time.framework.subscriptions :refer [register-virtual-sub]]
            [bed-time.framework.db :refer [db]]
            [bed-time.activities.activities :as activities]))

(defn- error []
  (activities/error (get-in @db [:page :activity-form :field])))

(register-virtual-sub [:page :activity-form :error] error)
