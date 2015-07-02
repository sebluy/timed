(ns bed-time.navbar
  (:require [bed-time.routing :refer [page->href]]
            [bed-time.framework.subscriptions :refer [subscribe]]
            [bed-time.util :as util]
            [bed-time.sessions.handlers :as session-handlers]))

(defn finish-session-button []
  (fn [current-session]
    [:button.btn.btn-danger.navbar-btn
     {:type     "button"
      :on-click #(session-handlers/finish-session current-session)}
     (str "Finish " (current-session :activity) " Session ")
     #_[:span.badge (util/time-str
                      (util/time-diff (current-session :start) @now))]]))

(defn current-session-nav []
  (let [current-session (subscribe [:current-session])]
    (fn []
      (if @current-session
        [:ul.nav.navbar-nav.navbar-right
         [:li [finish-session-button @current-session]]]))))

(defn navbar []
  (let [activities-href (page->href {:handler :activities})]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href activities-href} "Bed Time!"]]
      [:ul.nav.navbar-nav
       [:li [:a {:href activities-href} "Activities"]]]
      [current-session-nav]]]))

