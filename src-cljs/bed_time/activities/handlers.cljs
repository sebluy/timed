(ns bed-time.activities.handlers
  (:require [ajax.core :as ajax]
            [cljs.core.async :as async]
            [bed-time.activities.transitions :as transitions]
            [bed-time.framework.db :as db])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn delete-activity [activity]
  (let [response-chan (async/chan)]
    (ajax/POST
      "/delete-activity"
      {:params          {:activity activity}
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    (go
      (async/<! response-chan)
      (db/transition (transitions/delete-activity activity)))))

