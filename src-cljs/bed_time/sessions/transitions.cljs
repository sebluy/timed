(ns bed-time.sessions.transitions
  (:require [bed-time.sessions.sessions :as sessions]))

#_(defn update-session [{:keys [activity start] :as session}]
  (fn [db]
    (if (nil? (get-in db [:activities activity]))
      (assoc-in db [:activities activity]
                (assoc (sessions/sessions-map) start session))
      (assoc-in db [:activities activity start] session))))

(defn delete-session [{:keys [activity start]}]
  (fn [db]
    (update-in db [:activities activity] #(dissoc % start))))

#_(defn swap-session [old-session new-session]
  (fn [db]
    (update-in db [:activities (new-session :activity)]
               (fn [activity-sessions]
                 (-> activity-sessions
                     (dissoc (old-session :start))
                     (assoc (new-session :start) new-session))))))

(defn add-session [{:keys [activity start] :as session}]
  (fn [db]
    (assoc-in db [:activities activity start] session)))

(defn update-session [{:keys [activity start]} f]
  (fn [db]
    (update-in db [:activities activity start] f)))

