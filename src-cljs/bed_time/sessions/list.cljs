(ns bed-time.sessions.list
  (:require [bed-time.sessions.components :as components]
            [bed-time.sessions.form.components :as form]
            [bed-time.util :as util]
            [bed-time.framework.db :as db])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn- page-header [activity]
  [:div.page-header
   [:h1 activity
    [:p.pull-right.btn-toolbar
     [components/session-action-button activity]
     [components/new-session-form-button activity]
     [components/delete-activity-button activity]]]])

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
       [components/session-list @activity]])))

