(ns bed-time.routing
  (:require [bed-time.framework.events :refer [dispatch]]
            [bidi.bidi :as bidi]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [clojure.string :as string])
  (:import goog.History))

(defonce history (History.))

(defonce routes ["activities" {""              :activities
                               ["/" :activity] :activity}])

(defn- route->page [route]
  (bidi/match-route routes route))

(defn- page->route [page]
  (bidi/unmatch-pair routes {:handler (page :handler)
                             :params  (page :route-params)}))

(defn- route->href [route]
  (str "/#" route))

(defn page->href [page]
  (-> page
      page->route
      route->href))

(defn redirect [db page]
  (.setToken history (page->route page))
  (assoc db :page page))

(defn- initialize-route [history]
  (let [history-token (.getToken history)]
    (if (string/blank? history-token)
      (let [token (page->route {:handler :activities})]
        (.replaceToken history token)
        (dispatch {:handler :navigate-route :route token}))
      (dispatch {:handler :navigate-route :route history-token}))))

(defn hook-browser-navigation []
  (doto history
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (if (.-isNavigation event)
          (dispatch {:handler :navigate-route :route (.-token event)}))
        (.preventDefault event)))
    (.setEnabled true)
    (initialize-route)))

