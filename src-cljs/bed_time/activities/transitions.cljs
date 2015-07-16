(ns bed-time.activities.transitions)

(defn delete-activity [activity]
  (fn [db]
    (update-in db [:activities] #(dissoc % activity))))

(defn update-activity [activity f]
  (fn [db]
    (update-in db [:activities activity] f)))

