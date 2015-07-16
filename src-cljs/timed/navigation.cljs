(ns timed.navigation
  (:require [timed.routing :as routing]
            [timed.history :as history]
            [goog.events :as events]
            [clojure.string :as string]
            [timed.pages.transitions :as transitions]
            [timed.framework.db :as db])
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
