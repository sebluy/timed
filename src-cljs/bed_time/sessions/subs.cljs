(ns bed-time.sessions.subs
  (:require [bed-time.framework.subscriptions :refer [register-virtual-sub]]
            [re-frame.db :refer [app-db]]))

(defn- sessions []
  (let [activity (get-in @app-db [:page :route-params :activity])]
    (get-in @app-db [:activities activity])))

(register-virtual-sub [:page :sessions] sessions)
