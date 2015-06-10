(ns bed-time.navbar
  (:require [bed-time.sessions.current :as current-session]
            [bed-time.state :as state]))

(defn end-session-button []
  [:input.btn.btn-sm.btn-danger.navbar-btn
   {:type     "button"
    :value    (str "End "
                   (@state/current-session :activity)
                   " Session")
    :on-click #(current-session/end-current)}])

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
       [:li [end-session-button]]])]])
