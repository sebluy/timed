(ns bed-time.pages
  (:require [bed-time.activities.list :as activity-list]
            [bed-time.sessions.list :as session-list]
            [re-frame.core :refer [subscribe dispatch]]
            [bed-time.navbar :as navbar])
  (:import goog.History))

(defonce pages {:activities activity-list/page
                :activity   session-list/page})

(defn- current-page []
  (let [page (subscribe [:page])]
    (fn []
      [(or (pages (:handler @page)) :div)])))

(defn view []
  [:div
   [navbar/navbar]
   [current-page]])
