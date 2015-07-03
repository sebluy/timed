(ns bed-time.sessions.transitions
  (:require [bed-time.sessions.sessions :as sessions]))

(defn update-session [{:keys [activity start] :as session}]
  (fn [db]
    (if (nil? (get-in db [:activities activity]))
      (assoc-in db [:activities activity]
                (assoc (sessions/sessions-map) start session))
      (assoc-in db [:activities activity start] session))))

(defn delete-session [{:keys [activity start]}]
  (fn [db]
    (update-in db [:activities activity] #(dissoc % start))))

(defn swap-session [old-session new-session]
  (fn [db]
    (update-in db [:activities (new-session :activity)]
               (fn [activity-sessions]
                 (-> activity-sessions
                     (dissoc (old-session :start))
                     (assoc (new-session :start) new-session))))))
