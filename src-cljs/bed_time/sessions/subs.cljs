(ns bed-time.sessions.subs
  (:require [bed-time.framework.subscriptions :refer [register-virtual-sub]]
            [bed-time.framework.db :refer [db]]))

(defn- sessions []
  (let [activity (get-in @db [:page :route-params :activity])]
    (get-in @db [:activities activity])))

(register-virtual-sub [:page :sessions] sessions)
