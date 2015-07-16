(ns bed-time.sessions.form.components
  (:require [bed-time.sessions.form.handlers :as handlers]
            [bed-time.util :as util]
            [bed-time.pages.components :as page-components])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn label [key]
  (with-subs [message [:page :session-form :fields key :message]
              error [:page :session-form :fields key :error]]
    (fn []
      [:label ({:start "Start: " :finish "Finish: "} key)
       (if @error
         [:span.label.label-danger @error]
         [:span.label.label-success @message])])))

(defn- input [key]
  (with-subs [text [:page :session-form :fields key :text]]
    (fn []
      [:input.form-control
       {:type      "text"
        :value     @text
        :on-change #(handlers/update-field key (util/get-event-value %))}])))

(defn- form-group [key]
  [:div.form-group
   [label key]
   [input key]])

(defn- submit-button []
  (with-subs [pending [:page :session-form :pending]]
    (if @pending
      [page-components/pending-button]
      [:button.btn.btn-primary {:type "submit"} "Update"])))

(defn edit-form []
  [:form {:on-submit #(do (.preventDefault %) (handlers/submit))}
   [form-group :start]
   [form-group :finish]
   [:div.btn-toolbar
    [submit-button]
    [:button.btn.btn-danger
     {:type     "button"
      :on-click #(handlers/close)}
     "Cancel"]]])

