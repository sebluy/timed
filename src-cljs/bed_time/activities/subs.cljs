(ns bed-time.activities.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.sessions.sessions :as sessions])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn- pending-activities []
  (with-subs
    [activities [:activities]]
    (fn []
      (= :pending @activities))))

(defn- pending-delete [[activity]]
  (with-subs
    [activities [:activities]]
    (fn []
      (some? (get-in @activities [activity :pending])))))

(defn- confirmed []
  (with-subs
    [activities [:activities]]
    (fn []
      (if (= @activities :pending)
        :pending
        (into {}
              (filter
                (fn [[_ activity]]
                  (some (fn [session]
                          (not (= (get-in session [:pending :action]) :start)))
                        (vals (activity :sessions))))
                @activities))))))

(db/register-derived-query [:pending :activities] pending-activities)
(db/register-derived-query [:pending :delete-activity] pending-delete)
(db/register-derived-query [:confirmed-activities] confirmed)
