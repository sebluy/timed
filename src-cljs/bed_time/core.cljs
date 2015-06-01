(ns bed-time.core
  (:require [bed-time.bed-time :refer [bed-time-page]]
            [reagent.core :as reagent]
            [bed-time.plot :as plot])
  (:import goog.history.Html5History))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand "Bed Time!"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      [:li [:a "Bed Times"]]]]]])

(defn mount-components []
  (reagent/render-component [navbar] (.getElementById js/document "navbar"))
  (reagent/render-component [bed-time-page] (.getElementById js/document "app")))

(defn init! []
  (plot/init)
  (mount-components))

