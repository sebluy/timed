(ns timed.pages.activity
  (:require [timed.sessions.form.components :as form]
            [timed.sessions.components :as session-components]
            [timed.activities.components :as activity-components])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn- page-header [activity]
  (with-subs
    [activity [:page :route-params :activity]]
    (fn []
      [:div.page-header
       [:h1 @activity
        [:p.pull-right.btn-toolbar
         [session-components/action-button @activity "" :header]
         [session-components/new-button @activity]
         [activity-components/delete-button @activity]]]])))

(defn edit-form-slot []
  (with-subs [edit-form [:page :session-form]]
    (fn []
      (if @edit-form
        [form/edit-form]))))

(defn page []
  [:div.col-md-8.col-md-offset-2
   [page-header]
   [edit-form-slot]
   [session-components/session-list-slot]])

