(ns bed-time.core
  (:require [bed-time.state :as state]
            [bed-time.navbar :as navbar]
            [bed-time.activities.activities :as activities]
            [bed-time.activities.list :as activities-list]
            [bed-time.activities.show :as activities-show]
            [reagent.core :as reagent]
            [bidi.bidi :as bidi]
            [goog.events :as events]
            [goog.dom :as dom]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame])
  (:require-macros [reagent.ratom :as reaction])
  (:import goog.History))

(def routes ["/#" {"activities" {"" :activities
                                 ["/" :activity] :activity}}])

(def pages {:activities activities-list/page
            :activity activities-show/page})

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
    (pages (:handler @page))))

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

(defn mount-components []
  (reagent/render-component [navbar/navbar] (dom/getElement "navbar"))
  (reagent/render-component [current-page] (dom/getElement "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-components)
  (re-frame/dispatch [:get-activities]))

