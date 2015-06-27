(ns bed-time.subs
  (:require [re-frame.db :refer [app-db]]
            [bed-time.framework.subscriptions :refer [register-virtual-sub]]
            [bed-time.activities.form.subs]
            [bed-time.sessions.subs]
            [bed-time.activities.activities :as activities]
            [bed-time.sessions.sessions :as sessions]))

(defn- current-session []
  (sessions/current (get-in @app-db [:activities])))

(defn- aggregates [path]
  (-> (get-in @app-db [:activities])
      (activities/build-aggregates)
      (activities/add-week-total)
      (get-in path)))

(register-virtual-sub [:aggregates] aggregates)
(register-virtual-sub [:current-session] current-session)


