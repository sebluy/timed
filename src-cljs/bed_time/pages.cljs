(ns bed-time.pages
  (:require [bed-time.activities.list :as activity-list]
            [bed-time.sessions.list :as session-list]
            [bed-time.framework.subscriptions :refer [subscribe]]
            [bed-time.navbar :as navbar]))

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
