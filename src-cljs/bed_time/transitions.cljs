(ns bed-time.transitions
  (:require [bed-time.history :as history]
            [bed-time.framework.db :as db]))

(defn set-page [page]
  (fn [db]
    (assoc db :page page)))

#_(defn tick [db]
  (assoc-in db [:tick :now] (js/Date.)))

#_(defn start-tick [db]
  (assoc db :tick {:now      (js/Date.)
                   :interval (js/setInterval #(db/transition tick) 1000)}))

#_(defn stop-tick [db]
  (js/clearInterval (get-in db [:tick :interval]))
  (dissoc db :tick))

(defn update-activities [activities]
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

