(ns bed-time.sessions.form.components
  (:require [bed-time.sessions.form.transitions :as transitions]
            [bed-time.sessions.form.handlers :as handlers]
            [bed-time.util :as util]
            [bed-time.framework.db :as db]))

(defn- label [pre-label text]
  (let [value (util/str->date @text)
        error (if (util/datetime-invalid? value) "Invalid Time")
        date-str (util/date->str value)]
    [:label pre-label
     (if value
       (if error
         [:span.label.label-danger error]
         [:span.label.label-success date-str]))]))

(defn- input [key text]
  [:input.form-control
   {:type      "text"
    :value     @text
    :on-change #(db/transition
                 (transitions/update-field key (util/get-event-value %)))}])

(defn- form-group [key pre-label]
  (let [text (db/subscribe [:page :session-form :fields key])]
    [:div.form-group
     [label pre-label text]
     [input key text]]))

(defn- submit [event]
  (.preventDefault event)
  (handlers/submit))

(defn edit-form []
  [:form {:on-submit submit}
   [form-group :start "Start: "]
   [form-group :finish "Finish: "]
   [:div.btn-toolbar
    [:button.btn.btn-primary {:type "submit"} "Update"]
    [:button.btn.btn-danger
     {:type     "button"
      :on-click #(db/transition transitions/close)}
     "Cancel"]]])


