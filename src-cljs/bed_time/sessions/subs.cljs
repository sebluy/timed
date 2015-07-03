(ns bed-time.sessions.subs
  (:require [bed-time.framework.db :as db]))

(defn- sessions []
  (let [activity (db/query [:page :route-params :activity])]
    (db/query [:activities activity])))

(db/register-virtual-sub [:page :sessions] sessions)
