(ns bed-time.activities.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.sessions.sessions :as sessions]))

(defn- pending-activities []
  (= :pending (db/query [:activities])))

(db/register-derived-query [:pending :activities] pending-activities)
