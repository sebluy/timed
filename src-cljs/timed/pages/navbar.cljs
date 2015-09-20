(ns timed.pages.navbar
  (:require [timed.routing :refer [page->href]]
            [timed.activities.form.components :as activity-form-components]
            [timed.pages.handlers :as page-handlers]
            [timed.util :as util]
            [timed.sessions.handlers :as session-handlers]
            [sigsub.core :as sigsub :include-macros :true]))

(defn finish-session-button []
  (sigsub/with-reagent-subs
    [current-session [:current-session]
     time-spent [:current-session-time-spent]]
    (fn []
      [:button.btn.btn-danger.navbar-btn
       {:type     "button"
        :on-click #(session-handlers/finish-session @current-session)}
       "Finish " [:span.badge (util/time-str @time-spent)]])))

(defn- form-slot []
  (sigsub/with-reagent-subs
    [current-session [:current-session]]
    (fn []
      (if @current-session
        [finish-session-button]
        [activity-form-components/form]))))

(defn status []
  (sigsub/with-reagent-subs
    [status [:status]]
    [:h3.navbar-text
     (if (= @status :online)
       [:span.label.label-success.label-as-badge "Online"]
       [:span.label.label-danger.label-as-badge "Offline"])]))

(defn remote-link []
  (sigsub/with-reagent-subs
    [pending [:remote :pending]
     queued [:remote :queued]]
    (fn []
      [:a {:href (page->href {:handler :remote})}
       "Remote "
        [:span.label.label-success.label-as-badge (count @pending)]
        [:span.label.label-warning.label-as-badge (count @queued)]])))

(defn navbar []
  (let [activities-href (page->href {:handler :activities})]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href activities-href} "Timed"]]
      [:ul.nav.navbar-nav
       [:li [:a {:href (page->href {:handler :today})} "Today"]]
       [:li [:a {:href activities-href} "Activities"]]
       [:li [remote-link]]]
      [:ul.nav.navbar-nav.pull-right
       [:li [form-slot]]
       [:li [status]]]]]))

