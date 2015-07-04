(ns bed-time.navbar
  (:require [bed-time.routing :refer [page->href]]
            [bed-time.activities.form.components :as activity-form-components]
            [bed-time.framework.db :as db]
            [bed-time.sessions.components :as session-components]))

(defn navbar-finish-session-button [current-session]
  [session-components/finish-session-button
    current-session
    (str "Finish " (current-session :activity) " Session ")
    "navbar-btn"])
     #_[:span.badge (util/time-str
                      (util/time-diff (current-session :start) @now))]

(defn current-session-nav []
  (let [current-session (db/subscribe [:current-session])]
    (fn []
      (if @current-session
        [:div.navbar-right (navbar-finish-session-button @current-session)]
        [activity-form-components/form]))))

(defn navbar []
  (let [activities-href (page->href {:handler :activities})]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href activities-href} "Bed Time!"]]
      [:ul.nav.navbar-nav
       [:li [:a {:href activities-href} "Activities"]]]
      [current-session-nav]]]))

