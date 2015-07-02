(ns bed-time.sessions.transitions)

(defn update-session [{:keys [activity start] :as session}]
  (fn [db]
    (assoc-in db [:activities activity start] session)))

(defn delete-session [{:keys [activity start]}]
  (fn [db]
    (update-in db [:activities activity] #(dissoc % start))))

(defn swap-session [{:keys [old-start]}
                    {:keys [activity new-start] :as new-session}]
  (fn [db]
    (update-in db [:activities activity]
               (fn [activity-sessions]
                 (-> activity-sessions
                     (dissoc old-start)
                     (assoc new-start new-session))))))
