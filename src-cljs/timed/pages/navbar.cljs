(ns timed.pages.navbar
  (:require [timed.routing :refer [page->href]]
            [timed.activities.form.components :as activity-form-components]
            [timed.pages.handlers :as page-handlers]
            [timed.util :as util]
            [timed.sessions.handlers :as session-handlers])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn finish-session-button []
  (with-subs
    [current-session [:current-session]
     time-spent [:current-session-time-spent]]
    (fn []
      [:button.btn.btn-danger.navbar-btn
       {:type     "button"
        :on-click #(session-handlers/finish-session @current-session)}
       "Finish " [:span.badge (util/time-str @time-spent)]])))

(defn- form-slot []
  (with-subs
    [current-session [:current-session]]
    (fn []
      (if @current-session
        [finish-session-button]
        [activity-form-components/form]))))

(defn go-offline-button []
  [:input.btn.btn-danger.navbar-btn
   {:type  "button"
    :value "Go Offline"
    :on-click #(page-handlers/go-offline)}])

(defn go-online-button []
  [:input.btn.btn-success.navbar-btn
   {:type  "button"
    :value "Go Online"
    :on-click #(page-handlers/go-online)}])

(defn mode-button-slot []
  (with-subs
    [mode [:mode]]
    (if (= @mode :online)
      (go-offline-button)
      (go-online-button))))

(defn navbar []
  (let [activities-href (page->href {:handler :activities})]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.container
      [:div.navbar-header
       [:a.navbar-brand {:href activities-href} "Timed"]]
      [:ul.nav.navbar-nav
       [:li [:a {:href activities-href} "Activities"]]]
      [:ul.nav.navbar-nav.pull-right
       [:li [form-slot]]
       [:li [mode-button-slot]]]]]))

