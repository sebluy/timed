(ns bed-time.core
  (:require [bed-time.navbar :as navbar]
            [bed-time.activities.subs :as activity-subs]
            [bed-time.activities.handlers :as activity-handlers]
            [bed-time.activities.list :as activity-list]
            [bed-time.sessions.list :as session-list]
            [bed-time.sessions.handlers :as session-handlers]
            [bed-time.sessions.subs :as session-subs]
            [reagent.core :as reagent]
            [bidi.bidi :as bidi]
            [goog.events :as events]
            [goog.dom :as dom]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame])
  (:require-macros [reagent.ratom :as reaction])
  (:import goog.History))

(def routes ["/#" {"activities" {""              :activities
                                 ["/" :activity] :activity}}])

(def pages {:activities activity-list/page
            :activity   session-list/page})

(defn route->page [route]
  (bidi/match-route routes route))

(re-frame/register-sub
  :page
  (fn [db _]
    (reaction/reaction (@db :page))))

(re-frame/register-handler
  :set-page
  (fn [db [_ page]]
    (merge db
           {:page page}
           (if (= (page :handler) :activities)
             {:activity-form {:error nil :field nil}}))))

(defn set-page! [route]
  (re-frame/dispatch [:set-page (route->page (str "/#" route))]))

(defn current-page []
  (let [page (re-frame/subscribe [:page])]
    (fn []
      [(or (pages (:handler @page)) :div)])))

(defn hook-browser-navigation! []
  (let [history (History.)
        token (.getToken history)]
    (if (= token "")
      (do
        (.replaceToken history "activities")
        (set-page! "activities"))
      (set-page! token))
    (doto history
      (events/listen
        EventType/NAVIGATE
        (fn [event]
          (set-page! (.-token event))))
      (.setEnabled true))))

(defn screen []
  [:div
   [navbar/navbar]
   [current-page]])

(defn mount-components []
  (reagent/render-component [screen] (dom/getElement "app")))

(defn init! []
  (hook-browser-navigation!)
  (session-handlers/register)
  (session-subs/register)
  (activity-handlers/register)
  (activity-subs/register)
  (mount-components)
  (re-frame/dispatch [:get-activities]))

