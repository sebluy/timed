(ns bed-time.pages.activity
  (:require [bed-time.framework.db :as db]
            [bed-time.sessions.form.components :as form]
            [bed-time.sessions.components :as session-components]
            [bed-time.activities.components :as activity-components]))

(defn- page-header [activity]
  [:div.page-header
   [:h1 activity
    [:p.pull-right.btn-toolbar
     [session-components/action-button activity]
     [session-components/new-button activity]
     [activity-components/delete-button activity]]]])

(defn edit-form-slot []
  (let [edit-form (db/subscribe [:page :session-form])]
    (fn []
      (if @edit-form
        [form/edit-form]))))

(defn page []
  (let [activity (db/subscribe [:page :route-params :activity])]
    (fn []
      [:div.col-md-8.col-md-offset-2
       [page-header @activity]
       [edit-form-slot]
       [session-components/session-list @activity]])))

