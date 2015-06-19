(ns bed-time.core
  (:require [bed-time.navbar :as navbar]
            [bed-time.activities.list :as activity-list]
            [bed-time.sessions.list :as session-list]
            [bed-time.handlers :as handlers]
            [bed-time.subs :as subs]
            [reagent.core :as reagent]
            [bidi.bidi :as bidi]
            [goog.events :as events]
            [goog.dom :as dom]
            [goog.history.EventType :as EventType]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as string])
  (:import goog.History))

(def routes ["/#" {"activities" {""              :activities
                                 ["/" :activity] :activity}}])

(def pages {:activities activity-list/page
            :activity   session-list/page})

(defn route->page [route]
  (bidi/match-route routes route))

(defn dispatch-route [route]
  (dispatch [:set-page (route->page (str "/#" route))]))

(defn current-page []
  (let [page (subscribe [:page])]
    (fn []
      [(or (pages (:handler @page)) :div)])))

(defn initialize-route [history]
  (let [token (.getToken history)]
    (if (string/blank? token)
      (do
        (.replaceToken history "activities")
        (dispatch-route "activities"))
      (dispatch-route token))))

(defn hook-browser-navigation []
  (doto (History.)
    (initialize-route)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (dispatch-route (.-token event))))
    (.setEnabled true)))

(defn screen []
  [:div
   [navbar/navbar]
   [current-page]])

(defn mount-components []
  (reagent/render-component [screen] (dom/getElement "app")))

(defn register-handlers-and-subs []
  (handlers/register)
  (subs/register))

(defn init! []
  (hook-browser-navigation)
  (register-handlers-and-subs)
  (mount-components)
  (dispatch [:get-activities]))

