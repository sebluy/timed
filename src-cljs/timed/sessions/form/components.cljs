(ns timed.sessions.form.components
  (:require [timed.sessions.form.handlers :as handlers]
            [timed.util :as util])
  (:require-macros [timed.macros :refer [with-subs]]))

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
  [:button.btn.btn-primary {:type "submit"} "Update"])

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

