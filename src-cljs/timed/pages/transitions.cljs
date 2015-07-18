(ns timed.pages.transitions
  (:require [timed.history :as history]
            [timed.framework.db :as db]
            [timed.sessions.sessions :as sessions]))

(defn set-page [page]
  (fn [db]
    (assoc db :page page)))

(defn- tick [db]
  (assoc-in db [:tick :now] (js/Date.)))

(defn start-tick [db]
  (assoc db :tick {:now      (js/Date.)
                   :interval (js/setInterval #(db/transition tick) 1000)}))

(defn stop-tick [db]
  (js/clearInterval (get-in db [:tick :interval]))
  (dissoc db :tick))

(defn update-activities [activities]
  (fn [db]
    (assoc db :activities activities)))

(defn redirect [page]
  (history/replace-token page)
  (set-page page))

