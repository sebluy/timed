(ns bed-time.pages
  (:require [bed-time.activities.list :as activity-list]
            [bed-time.sessions.list :as session-list]
            [re-frame.core :refer [subscribe dispatch]]
            [re-frame.db :refer [app-db]]
            [bed-time.navbar :as navbar]
            [bed-time.sessions.current :as current])
  (:require-macros [reagent.ratom :refer [reaction]])
  (:import goog.History))

(defonce pages {:activities activity-list/page
                :activity   session-list/page})

(defn- current-page [reactions]
  (let [page-component (pages (:handler @(reactions :page)))]
    (if page-component
      [page-component reactions]
      [:div])))

(defn view []
  (let [activities (reaction (@app-db :activities))
        current-session (reaction (current/extract-current @activities))
        page (reaction (@app-db :page))]
    (fn []
      [:div
       [navbar/navbar current-session]
       [current-page {:activities     activities
                      :current-sesion current-session
                      :page           page}]])))
