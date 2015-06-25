(ns bed-time.activities.form.subs
  (:require [bed-time.framework.subscriptions :refer [register-virtual-sub]]
            [bed-time.activities.activities :as activities]
            [re-frame.db :refer [app-db]]))

(defn- error []
  (activities/error (get-in @app-db [:page :activity-form :field])))

(register-virtual-sub [:page :activity-form :error] error)
