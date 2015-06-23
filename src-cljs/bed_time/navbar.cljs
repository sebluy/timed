(ns bed-time.navbar
  (:require [bed-time.routing :refer [page->href]]
            [re-frame.core :refer [dispatch subscribe]]))

(defn finish-session-button [session]
  [:input.btn.btn-sm.btn-danger.navbar-btn
   {:type     "button"
    :value    (str "Finish " (session :activity) " Session")
    :on-click #(dispatch [:finish-session session])}])

(defn current-session-nav [current-session-reaction]
  (let [current-session @current-session-reaction]
    (if current-session
      [:ul.nav.navbar-nav.navbar-right
       [:li (finish-session-button current-session)]])))

(defn navbar [current-session-reaction]
  (let [activities-href (page->href {:handler :activities})]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href activities-href} "Bed Time!"]]
      [:ul.nav.navbar-nav
       [:li [:a {:href activities-href} "Activities"]]]
      [current-session-nav current-session-reaction]]]))

