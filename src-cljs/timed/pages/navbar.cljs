(ns timed.pages.navbar
  (:require [timed.routing :refer [page->href]]
            [timed.activities.form.components :as activity-form-components]
            [timed.util :as util]
            [timed.sessions.sessions :as sessions]
            [timed.sessions.handlers :as session-handlers]
            [timed.pages.components :as page-components])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn finish-session-button []
  (with-subs
    [current-session [:current-session]
     time-spent [:current-session-time-spent]]
    (fn []
      [:button.btn.btn-danger.navbar-btn
       {:type     "button"
        :on-click #(session-handlers/finish-session @current-session :navbar)}
       "Finish " [:span.badge (util/time-str @time-spent)]])))

(defn- finish-session-button-slot []
  (with-subs
    [status [:navbar-finish-status]]
    (fn []
      (condp = @status
        :visible [finish-session-button]
        :pending [page-components/pending-button "navbar-btn"]
        :hidden nil))))

(defn- form-slot []
  (with-subs
    [current-session [:current-session]]
    (fn []
      [:div.navbar-right
       (if @current-session
         [finish-session-button-slot]
         [activity-form-components/form])])))

(defn navbar []
  (let [activities-href (page->href {:handler :activities})]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href activities-href} "Timed"]]
      [:ul.nav.navbar-nav
       [:li [:a {:href activities-href} "Activities"]]]
      [form-slot]]]))

