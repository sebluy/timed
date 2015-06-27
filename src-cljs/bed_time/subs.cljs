(ns bed-time.subs
  (:require [bed-time.framework.subscriptions :refer [register-virtual-sub]]
            [bed-time.framework.db :refer [db]]
            [bed-time.activities.form.subs]
            [bed-time.sessions.subs]
            [bed-time.activities.activities :as activities]
            [bed-time.sessions.sessions :as sessions]))

(defn- current-session []
  (sessions/current (get-in @db [:activities])))

(defn- aggregates [path]
  (-> (get-in @db [:activities])
      (activities/build-aggregates)
      (activities/add-week-total)
      (get-in path)))

(register-virtual-sub [:aggregates] aggregates)
(register-virtual-sub [:current-session] current-session)


