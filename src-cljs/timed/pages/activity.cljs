(ns timed.pages.activity
  (:require [timed.sessions.form.components :as form]
            [timed.sessions.components :as session-components]
            [timed.activities.components :as activity-components]
            [sigsub.core :as sigsub :include-macros :true]))

(defn- page-header [activity]
  (sigsub/with-reagent-subs
    [activity [:page :route-params :activity]]
    (fn []
      [:div.page-header
       [:h1 @activity
        [:p.pull-right.btn-toolbar
         [session-components/action-button @activity "" :header]
         [session-components/new-button @activity]
         [activity-components/delete-button @activity]]]])))

(defn edit-form-slot []
  (sigsub/with-reagent-subs
    [edit-form [:page :session-form]]
    (fn []
      (if @edit-form
        [form/edit-form]))))

(defn page []
  [:div.col-md-8.col-md-offset-2
   [page-header]
   [edit-form-slot]
   [session-components/session-list-slot]])

