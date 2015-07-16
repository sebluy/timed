(ns bed-time.pages.navbar
  (:require [bed-time.routing :refer [page->href]]
            [bed-time.activities.form.components :as activity-form-components])
  (:require-macros [bed-time.macros :refer [with-subs]]))

#_(defn navbar-finish-session-button [current-session]
  (println current-session)
  [session-components/finish-session-button
    current-session
   (util/time-str (sessions/time-spent current-session))
   "navbar-btn"])

(defn- form-slot []
  (with-subs
    [status [:page :activity-form :status]]
    (fn []
      (if-not (= @status :hidden)
        [activity-form-components/form]
        [:div]))))

(defn navbar []
  (let [activities-href (page->href {:handler :activities})]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href activities-href} "Bed Time!"]]
      [:ul.nav.navbar-nav
       [:li [:a {:href activities-href} "Activities"]]]
      [form-slot]]]))

