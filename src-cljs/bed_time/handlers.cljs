(ns bed-time.handlers
  (:require [bed-time.activities.handlers :as activity-handlers]
            [bed-time.sessions.handlers :as session-handlers]
            [re-frame.core :refer [register-handler trim-v]]))

(defn register []
  (activity-handlers/register)
  (session-handlers/register)

  (register-handler
    :set-page
    trim-v
    (fn [db [page]]
      (merge db
             {:page page}
             (if (= (page :handler) :activities)
               {:activity-form {:error nil :field nil}})))))

