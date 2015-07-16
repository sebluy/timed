(ns bed-time.pages.handlers
  (:require [bed-time.pages.transitions :as transitions]
            [bed-time.framework.db :as db]
            [bed-time.activities.activities :as activities]
            [cljs.core.async :as async]
            [bed-time.remote-handlers :as remote-handlers])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn get-activities []
  (db/transition (transitions/update-activities :pending))
  (go
    (let [raw (async/<! (remote-handlers/get-activities))
          sorted (activities/coerce-activities-to-sorted raw)]
      (db/transition
        (transitions/update-activities sorted)))))

