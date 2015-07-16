(ns bed-time.activities.handlers
  (:require [ajax.core :as ajax]
            [cljs.core.async :as async]
            [bed-time.activities.transitions :as activities-transitions]
            [bed-time.framework.db :as db]
            [bed-time.pages.transitions :as transitions])
  (:require-macros [cljs.core.async.macros :refer [go]]))

; todo: split into remote handlers

(defn delete-activity [activity]
  (db/transition (transitions/add-pending :delete-activity true))
  (let [response-chan (async/chan)]
    (ajax/POST
      "/delete-activity"
      {:params          {:activity activity}
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    (go
      (async/<! response-chan)
      (db/transition
        (comp
          (transitions/redirect {:handler :activities})
          (transitions/remove-pending :delete-activity)
          (activities-transitions/delete-activity activity))))))

