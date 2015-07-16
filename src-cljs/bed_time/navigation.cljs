(ns bed-time.navigation
  (:require [bed-time.routing :as routing]
            [bed-time.history :as history]
            [goog.events :as events]
            [clojure.string :as string]
            [bed-time.pages.transitions :as transitions]
            [bed-time.framework.db :as db])
  (:import goog.history.EventType))

(defn navigate [page]
  (db/transition (transitions/set-page page)))

(defn- initialize-route []
  (let [history-token (history/get-token)]
    (if (string/blank? history-token)
      (let [page {:handler :activities}]
        (history/replace-token page)
        (navigate page))
      (navigate (routing/route->page history-token)))))

(defn hook-browser []
  (doto history/history
    (events/listen
      EventType.NAVIGATE
      (fn [event]
        (navigate (routing/route->page (.-token event)))
        (.preventDefault event)))
    (.setEnabled true))
  (initialize-route))
