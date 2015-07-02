(ns bed-time.transitions)

(defn set-page [page]
  (fn [db]
    (assoc db :page page)))

(defn tick [now]
  (fn [db]
    (assoc db :tick now)))

(defn reload-activities [activities]
  (fn [db]
    (assoc db :activities activities)))

