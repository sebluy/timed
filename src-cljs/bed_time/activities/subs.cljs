(ns bed-time.activities.subs
  (:require [bed-time.framework.db :as db])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn- pending-activities []
  (with-subs
    [activities [:activities]]
    (fn []
      (= :pending @activities))))

(db/register-derived-query [:pending :activities] pending-activities)
