(ns bed-time.core
  (:require [bed-time.bed-time :refer [bed-time-page]]
            [reagent.core :as reagent]
            [reagent.session :as session]
            [secretary.core :as secretary])
  (:require-macros [secretary.core :refer [defroute]]))

(defn navbar []
      [:div.navbar.navbar-inverse.navbar-fixed-top
       [:div.container
        [:div.navbar-header
         [:a.navbar-brand
          {:on-click #(secretary/dispatch! "#/bed-times")}
          "Bed Time!"]]
        [:div.navbar-collapse.collapse
         [:ul.nav.navbar-nav
          [:li {:class (when (= :bed-times (session/get :page)) "active")}
           [:a {:on-click
                #(secretary/dispatch! "#/bed-times")} "Bed Times"]]
          [:li {:class (when (= :about (session/get :page)) "active")}
           [:a {:on-click #(secretary/dispatch! "#/about")} "About"]]]]]])

(defn about-page []
  [:div "this is the story of bed-time... work in progress"])

(def pages
  {:bed-times #'bed-time-page
   :about #'about-page})

(defn page []
  [(pages (session/get :page))])

(defroute "/bed-times" [] (session/put! :page :bed-times))
(defroute "/about" [] (session/put! :page :about))

(defn mount-components []
  (reagent/render-component [navbar] (.getElementById js/document "navbar"))
  (reagent/render-component [page] (.getElementById js/document "app")))

(defn init! []
  (secretary/set-config! :prefix "#")
  (session/put! :page :bed-times)
  (mount-components))

