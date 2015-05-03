(ns bed-time.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [reagent.session :as session]
            [reagent-forms.core :refer [bind-fields]]
            [ajax.core :refer [GET POST]])
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

(defn bed-times-updater [bed-times]
  (fn [response]
    (reset! bed-times (map (fn [time]
                             (.toUTCString (time :time)))
                           (response :bed-times)))))

(defn get-bed-times [bed-times]
  (println "getting /bed-times")
  (GET "/bed-times" {:handler (bed-times-updater bed-times)
                     :response-format :edn}))

(defn bed-times-page []
  (let [bed-times (atom '())]
    (get-bed-times bed-times)
    (fn []
      [:ul
       (for [bed-time @bed-times]
         ^{:key bed-time} [:li bed-time])])))

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


