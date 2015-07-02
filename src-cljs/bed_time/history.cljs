(ns bed-time.history
  (:require [bed-time.handlers :as handlers]
            [bed-time.routing :as routing]
            [goog.events :as events]
            [clojure.string :as string])
  (:import goog.History
           goog.history.EventType))

(defonce history (History.))

(defn- initialize-route [history]
  (let [history-token (.getToken history)]
    (if (string/blank? history-token)
      (let [token (routing/page->route {:handler :activities})]
        (.replaceToken history token)
        (handlers/navigate token)
      (handlers/navigate history-token)))))

(defn hook-browser []
  (doto history
    (events/listen
      EventType.NAVIGATE
      (fn [event]
        (handlers/navigate (.-token event))
        (.preventDefault event)))
    (.setEnabled true)
    (initialize-route)))
