(ns bed-time.core
  (:require [bed-time.bed-times :refer [bed-times-page]]
            [reagent.core :as reagent]
            [reagent.session :as session]
            [secretary.core :as secretary])
  (:require-macros [secretary.core :refer [defroute]]))

(defn navbar []
      [:div.navbar.navbar-inverse.navbar-fixed-top
       [:div.container
        [:div.navbar-header
         [:a.navbar-brand {:href "#/"} "bed-time"]]
        [:div.navbar-collapse.collapse
         [:ul.nav.navbar-nav
          [:li {:class (when (= :home (session/get :page)) "active")}
           [:a {:on-click #(secretary/dispatch! "#/")} "Home"]]
          [:li {:class (when (= :about (session/get :page)) "active")}
           [:a {:on-click #(secretary/dispatch! "#/about")} "About"]]
          [:li {:class (when (= :bed-times (session/get :page)) "active")}
           [:a {:on-click
                #(secretary/dispatch! "#/bed-times")} "Bed Times"]]]]]])

(defn about-page []
  [:div "this is the story of bed-time... work in progress"])

(defn home-page []
  [:div
   [:h2 "Welcome to ClojureScript"]])

(def pages
  {:home #'home-page
   :about #'about-page
   :bed-times #'bed-times-page})

(defn page []
  [(pages (session/get :page))])

(defroute "/" [] (session/put! :page :home))
(defroute "/about" [] (session/put! :page :about))
(defroute "/bed-times" [] (session/put! :page :bed-times))

(defn mount-components []
  (reagent/render-component [navbar] (.getElementById js/document "navbar"))
  (reagent/render-component [page] (.getElementById js/document "app")))

(defn init! []
  (secretary/set-config! :prefix "#")
  (session/put! :page :home)
  (mount-components))


