(ns bed-time.activities.handlers
  (:require [cljs.core.async :as async]
            [bed-time.framework.db :as db]
            [bed-time.activities.transitions :as transitions]
            [bed-time.pages.transitions :as page-transitions]
            [bed-time.remote-handlers :as remote-handlers])
  (:require-macros [cljs.core.async.macros :as async]))

; todo: split into remote handlers

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
        (transitions/delete-activity activity)))))

