(ns bed-time.navbar
  (:require [bed-time.sessions.sessions :as sessions]
            [bed-time.state :as state]))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:href "/#activities"} "Bed Time!"]]
    [:ul.nav.navbar-nav
     [:li [:a {:href "/#activities"} "Activities"]]]
;     [:li [:a {:href "/#list"} "List"]]]]])
;     [:li [:a {:href "/#time-slept-plot"} "Time Slept Plot"]]
;     [:li [:a {:href "/#bed-time-plot"} "Bed Time Plot"]]
;     [:li [:a {:href "/#wake-up-time-plot"} "Wake Up Time Plot"]]]
    (if @state/current-session
      [:ul.nav.navbar-nav.navbar-right
       [:li [sessions/end-session-button "navbar-btn"]]])]])
