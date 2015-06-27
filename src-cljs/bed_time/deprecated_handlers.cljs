(ns bed-time.handlers
  (:require [bed-time.activities.handlers]
            [bed-time.activities.form.handlers]
            [bed-time.sessions.handlers]
            [bed-time.sessions.form.handlers]
            [re-frame.core :refer [dispatch register-handler]]))

(register-handler
  :set-page
  (fn [db [_ page]]
    (merge db {:page page})))

(register-handler
  :start-tick
  (fn [db _]
    (let [interval (js/setInterval #(dispatch [:tick]) 1000)]
      (assoc db :tick {:now (js/Date.) :interval interval}))))

(register-handler
  :tick
  (fn [db _]
    (assoc-in db [:tick :now] (js/Date.))))

(register-handler
  :sync-tick
  (fn [db _]
    (js/clearInterval (get-in db [:tick :interval]))
    (dispatch [:start-tick])
    db))

