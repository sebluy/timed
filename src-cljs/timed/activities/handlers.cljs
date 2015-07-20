(ns timed.activities.handlers
  (:require [timed.framework.db :as db]
            [timed.activities.transitions :as transitions]
            [timed.pages.transitions :as page-transitions]
            [timed.remote-handlers :as remote-handlers]))

(defn delete-activity [activity]
  (db/transition
    (comp
      (page-transitions/redirect {:handler :activities})
      (if (= (:activity (db/query-once [:current-session])) activity)
        page-transitions/stop-tick
        identity)
      (transitions/delete-activity activity)))
  (remote-handlers/delete-activity activity))

