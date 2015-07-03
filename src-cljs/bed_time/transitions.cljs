(ns bed-time.transitions
  (:require [bed-time.history :as history]))

(defn set-page [page]
  (fn [db]
    (assoc db :page page)))

(defn tick [now]
  (fn [db]
    (assoc db :tick now)))

(defn reload-activities [activities]
  (fn [db]
    (assoc db :activities activities)))

(defn add-pending [key value]
  (fn [db]
    (assoc-in db [:pending key] value)))

(defn remove-pending [key]
  (fn [db]
    (update-in db [:pending] #(dissoc % key))))

(defn redirect [page]
  (history/replace-token page)
  (set-page page))

