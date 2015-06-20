(ns bed-time.navbar
  (:require [bed-time.sessions.sessions :as sessions]
            [re-frame.core :refer [dispatch subscribe]]))

(defn finish-session-button [session]
  [:input.btn.btn-sm.btn-danger.navbar-btn
   {:type     "button"
    :value    (str "Finish " (session :activity) " Session")
    :on-click #(dispatch [:finish-session session])}])

(defn current-session-nav []
  (let [current-session (subscribe [:current-session])]
    (fn []
      (if @current-session
        [:ul.nav.navbar-nav.navbar-right
         [:li (finish-session-button @current-session)]]))))

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
    [current-session-nav]]])

