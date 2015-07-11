(ns bed-time.sessions.form.components
  (:require [bed-time.sessions.form.transitions :as transitions]
            [bed-time.util :as util]
            [bed-time.components :as components]
            [bed-time.framework.db :as db])
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
        :on-change #(db/transition
                     (transitions/update-field key
                                               (util/get-event-value %)))}])))

(defn- form-group [key]
  [:div.form-group
   [label key]
   [input key]])

(defn- submit-button []
  (with-subs [pending [:pending :session-form]]
    (if @pending
      [components/pending-button]
      [:button.btn.btn-primary {:type "submit"} "Update"])))

(defn edit-form []
  [:form {:on-submit #(do (.preventDefault %) #_(handlers/submit))}
   [form-group :start]
   [form-group :finish]
   [:div.btn-toolbar
    [submit-button]
    [:button.btn.btn-danger
     {:type     "button"
      :on-click #(db/transition transitions/close)}
     "Cancel"]]])


