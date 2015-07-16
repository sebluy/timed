(ns timed.pages.handlers
  (:require [timed.pages.transitions :as transitions]
            [timed.framework.db :as db]
            [timed.activities.activities :as activities]
            [cljs.core.async :as async]
            [timed.remote-handlers :as remote-handlers])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn get-activities []
  (db/transition (transitions/update-activities :pending))
  (go
    (let [raw (async/<! (remote-handlers/get-activities))
          sorted (activities/coerce-activities-to-sorted raw)]
      (db/transition
        (transitions/update-activities sorted)))))

