(ns bed-time.sessions.transitions)

(defn delete-session [{:keys [activity start]}]
  (fn [db]
    (update-in db [:activities activity :sessions] #(dissoc % start))))

(defn add-session [{:keys [activity start] :as session}]
  (fn [db]
    (assoc-in db [:activities activity :sessions start] session)))

(defn update-session [{:keys [activity start]} f]
  (fn [db]
    (update-in db [:activities activity :sessions start] f)))

