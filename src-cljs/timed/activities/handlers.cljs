(ns timed.activities.handlers
  (:require [cljs.core.async :as async]
            [timed.framework.db :as db]
            [timed.activities.transitions :as transitions]
            [timed.pages.transitions :as page-transitions]
            [timed.remote-handlers :as remote-handlers])
  (:require-macros [cljs.core.async.macros :as async]))

(defn delete-activity [activity]
  (db/transition
    (transitions/update-activity
      activity
      #(assoc % :pending {:action :delete})))
  (async/go
    (async/<! (remote-handlers/delete-activity activity))
    (db/transition
      (comp
        (page-transitions/redirect {:handler :activities})
        (if (= (:activity (db/query-once [:current-session])) activity)
          page-transitions/stop-tick
          identity)
        (transitions/delete-activity activity)))))

