(ns bed-time.sessions.sessions
  (:require [bed-time.util :as util]
            [re-frame.core :refer [dispatch]]
            [ajax.core :refer [POST]]))

(defn sessions-map [] (sorted-map-by util/date-comparator))

(defn path->session [db activity start]
  {:activity activity
   :start    start
   :finish   (get-in db [:activities activity start])})

(defn map->vec [session-map]
  [(session-map :start) (session-map :finish)])

(defn valid? [{:keys [start finish activity]}]
  (not (or (util/datetime-invalid? start)
           (util/datetime-invalid? finish)
           (nil? activity))))

(defn current? [{finish :finish}]
  (nil? finish))

(defn- find-current-in-sessions [sessions]
  (loop [[_ session] (first sessions) sessions' (rest sessions)]
    (cond (current? session) session
          (empty sessions') nil
          :else (recur (first sessions') (rest sessions')))))

(defn- current [activities]
  (loop [[_ sessions] (first activities) activities' (rest activities)]
    (let [current (find-current-in-sessions sessions)]
      (cond current current
            (empty activities') nil
            :else (recur (first activities') (rest activities'))))))

(defn new-session [activity]
  {:activity activity :start (js/Date.) :finish nil :new true})

(defn post-update-session [session handler]
  (POST "/update-session"
        {:params          {:session session}
         :handler         #(handler % session)
         :format          :edn
         :response-format :edn}))

(defn time-spent [{:keys [start finish] :as session}]
  (if (current? session)
    0
    (util/time-diff start finish)))

(defn start-session-button [activity]
  [:input.btn.btn-sm.btn-success
   {:type     "button"
    :value    "Start Session"
    :on-click #(dispatch [:new-session activity])}])
