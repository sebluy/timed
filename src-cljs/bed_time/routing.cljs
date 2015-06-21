(ns bed-time.routing
  (:require [bed-time.activities.list :as activity-list]
            [bed-time.sessions.list :as session-list]
            [bidi.bidi :as bidi]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as string])
  (:import goog.History))

(defonce history (History.))

(defonce routes ["activities" {""              :activities
                               ["/" :activity] :activity}])

(defonce pages {:activities activity-list/page
                :activity   session-list/page})

(defn- route->page [route]
  (bidi/match-route routes route))

(defn- page->route [page]
  (bidi/unmatch-pair routes {:handler (page :handler)
                             :params (page :route-params)}))

(defn redirect [db page]
  (.setToken history (page->route page))
  (assoc db :page page))

(defn dispatch-route [route]
  (dispatch [:set-page (route->page route)]))

(defn current-page []
  (let [page (subscribe [:page])]
    (fn []
      [(or (pages (:handler @page)) :div)])))

(defn- initialize-route [history]
  (let [token (.getToken history)]
    (if (string/blank? token)
      (do
        (.replaceToken history "activities")
        (dispatch-route "activities"))
      (dispatch-route token))))

(defn hook-browser-navigation []
  (doto history
    (initialize-route)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (dispatch-route (.-token event))))
    (.setEnabled true)))

