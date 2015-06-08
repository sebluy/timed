(ns bed-time.core
  (:require [bed-time.state :as state]
            [bed-time.navbar :as navbar]
            [bed-time.activities.core :as activities]
            [bed-time.activities.list :as activities-list]
            [bed-time.activities.show :as activities-show]
            [reagent.core :as reagent]
            [bidi.bidi :as bidi]
            [goog.events :as events]
            [goog.dom :as dom]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(def routes ["/#" {"activities" {"" :activities
                                 ["/" :activity] :activity}}])

(def pages {:activities activities-list/page
            :activity activities-show/page})

(defn route->page [route]
  (let [match (bidi/match-route routes route)
        params (match :route-params)
        page (pages (match :handler))]
    (if params
      (page params)
      page)))

(defn set-page! [token]
  (swap! state/state assoc :page (route->page (str "/#" token))))

(defn current-page []
  [(@state/state :page)])

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (set-page! (.-token event))))
    (.setEnabled true)))

(defn mount-components []
  (reagent/render-component [navbar/navbar] (dom/getElement "navbar"))
  (reagent/render-component [current-page] (dom/getElement "app")))

(defn init! []
  (let [history (hook-browser-navigation!)
        token (.getToken history)]
    (if (= token "")
      (do
        (.replaceToken history "activities")
        (set-page! "activities"))
      (set-page! token)))
  (mount-components)
  (activities/get-activities))

