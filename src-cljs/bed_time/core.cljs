(ns bed-time.core
  (:require [bed-time.plot.time-slept :as time-slept-plot]
            [bed-time.plot.bed-time :as bed-time-plot]
            [bed-time.plot.wake-up-time :as wake-up-time-plot]
            [bed-time.plot.plot :as plot]
            [bed-time.state :as state]
            [bed-time.days :as days]
            [bed-time.navbar :as navbar]
            [bed-time.activites :as activities]
            [bed-time.day-list :as day-list]
            [reagent.core :as reagent]
            [goog.events :as events]
            [goog.dom :as dom]
            [goog.history.EventType :as EventType])
  (:import goog.History))

; Replace ghetto-rig with bidi when neccessary
(def pages
  {"activities" activities/page
   "time-slept-plot" time-slept-plot/page
   "bed-time-plot" bed-time-plot/page
   "wake-up-time-plot" wake-up-time-plot/page
   "list" day-list/page})

(defn set-page! [page]
  (swap! state/state assoc :page (pages page)))

(defn current-page []
  [(@state/state :page)])

(defn mount-components []
  (reagent/render-component [navbar/navbar] (dom/getElement "navbar"))
  (reagent/render-component [current-page] (dom/getElement "app")))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (set-page! (.-token event))))
    (.setEnabled true)))

(defn init! []
  (let [history (hook-browser-navigation!)
        token (.getToken history)]
    (if (= token "")
      (do
        (.replaceToken history "list")
        (set-page! "list"))
      (set-page! token)))
  (mount-components)
  (activities/get-activities)
  (days/get-days)
  (plot/init))

