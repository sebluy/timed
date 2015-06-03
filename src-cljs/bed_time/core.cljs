(ns bed-time.core
  (:require [bed-time.bed-time :as bed-time]
            [reagent.core :as reagent]
            [bed-time.plot :as plot]
            [bed-time.state :as state]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(defn my-page []
  [:h1 "It's working... It's working..."])

(def pages
  {"plot" #'my-page
   "list" #'bed-time/bed-time-page})

(defn set-page! [page]
  (swap! state/state assoc :page (pages page)))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:href "/#"} "Bed Time!"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      [:li [:a {:href "/#my-page"} "My Page"]]]]]])

(defn page []
  [(@state/state :page)])

(defn mount-components []
  (reagent/render-component [#'navbar] (.getElementById js/document "navbar"))
  (reagent/render-component [#'page] (.getElementById js/document "app")))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (set-page! (.-token event))))
    (.setEnabled true)))

(defn init! []
  (let [history (hook-browser-navigation!)]
    (set-page! (.getToken history)))
  (mount-components)
  (plot/init))

