(ns bed-time.handlers
  (:require [cljs.core.async
             :refer [<! put! chan]]
            [ajax.core :refer [GET]]
            [bed-time.transitions :as transitions]
            [bed-time.framework.db :as db]
            [bed-time.activities.activities :as activities])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn get-activities []
  (db/transition (transitions/add-pending :get-activities true))
  (let [response-chan (chan)]
    (GET "/activities"
         {:handler #(put! response-chan %)
          :response-format :edn})
    (go
      (let [activities
            (activities/coerce-activities-to-sorted (<! response-chan))]
        (db/transition
          (comp
            (transitions/remove-pending :get-activities)
            (transitions/reload-activities activities)))))))

