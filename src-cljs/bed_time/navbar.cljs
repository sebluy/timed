(ns bed-time.navbar
  (:require [bed-time.routing :refer [page->href]]
            [bed-time.activities.form.components :as activity-form-components]
            [bed-time.framework.db :as db]
            [bed-time.sessions.components :as session-components]
            [bed-time.util :as util]
            [bed-time.sessions.sessions :as sessions])
  (:require-macros [bed-time.macros :refer [with-subs]]))

#_(defn navbar-finish-session-button [current-session]
  (println current-session)
  [session-components/finish-session-button
    current-session
   (util/time-str (sessions/time-spent current-session))
   "navbar-btn"])

(defn current-session-nav []
  (with-subs
    [activity-form-visible? [:activity-form-visible?]]
    (fn []
      (if @activity-form-visible?
        [activity-form-components/form]
        [:div] #_[:div.navbar-right [navbar-finish-session-button @current-session]]))))

(defn navbar []
  (let [activities-href (page->href {:handler :activities})]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href activities-href} "Bed Time!"]]
      [:ul.nav.navbar-nav
       [:li [:a {:href activities-href} "Activities"]]]
       [current-session-nav]]]))


