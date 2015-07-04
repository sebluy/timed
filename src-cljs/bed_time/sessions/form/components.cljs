(ns bed-time.sessions.form.components
  (:require [bed-time.sessions.form.transitions :as transitions]
            [bed-time.sessions.form.handlers :as handlers]
            [bed-time.util :as util]
            [bed-time.framework.db :as db]
            [bed-time.sessions.components :as session-components]))

(defn label [key]
  (let [message (db/subscribe [:page :session-form :fields key :message])
        error (db/subscribe [:page :session-form :fields key :error])]
    [:label ({:start "Start: " :finish "Finish: "} key)
     (if @error
       [:span.label.label-danger @error]
       [:span.label.label-success @message])]))

(defn- input [key]
  (let [text (db/subscribe [:page :session-form :fields key :text])]
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

(defn- submit [event]
  (.preventDefault event)
  (handlers/submit))

(defn- submit-button []
  (let [pending (db/subscribe [:pending :session-form])]
    (if @pending
      [session-components/pending-button]
      [:button.btn.btn-primary {:type "submit"} "Update"])))

(defn edit-form []
  [:form {:on-submit submit}
   [form-group :start]
   [form-group :finish]
   [:div.btn-toolbar
    [submit-button]
    [:button.btn.btn-danger
     {:type     "button"
      :on-click #(db/transition transitions/close)}
     "Cancel"]]])


