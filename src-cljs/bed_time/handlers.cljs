(ns bed-time.handlers
  (:require [cljs.core.async
             :refer [<! put! chan]]
            [ajax.core :refer [GET]]
            [bed-time.transitions :as transitions]
            [bed-time.framework.db :as db]
            [bed-time.activities.activities :as activities]
            [bed-time.routing :as routing]
            [bed-time.sessions.sessions :as sessions])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn navigate [route]
  (let [page (routing/route->page route)]
    (db/transition (transitions/set-page page))))

(defn get-activities []
  (let [response-chan (chan)]
    (GET "/activities"
         {:handler #(put! response-chan %)
          :response-format :edn})
    (go
      (let [activities (activities/coerce-activities-to-sorted
                         (<! response-chan))]
        (db/transition (transitions/reload-activities activities))))))

