(ns bed-time.pages
  (:require [bed-time.activities.list :as activity-list]
            [bed-time.sessions.list :as session-list]
            [bed-time.framework.subscriptions :refer [subscribe]]
            [re-frame.db :refer [app-db]]
            [bed-time.navbar :as navbar])
  (:require-macros [reagent.ratom :refer [reaction]])
  (:import goog.History))

(defonce pages {:activities activity-list/page
                :activity   session-list/page})

(defn- current-page []
  (let [handler (subscribe [:page :handler])]
    (fn []
      [(or (pages @handler) :div)])))

(defn view []
  [:div
   [navbar/navbar]
   [current-page]])
