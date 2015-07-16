(ns bed-time.activities.subs
  (:require [bed-time.framework.db :as db])
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

(db/register-derived-query [:pending :activities] pending-activities)
(db/register-derived-query [:pending :delete-activity] pending-delete)
