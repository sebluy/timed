(ns bed-time.sessions.form.components
  (:require [bed-time.framework.subscriptions :refer [subscribe]]
            [bed-time.util :as util]))

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
    #_:on-change #_(dispatch-sync
                 [:change-session-form-field
                  key (util/get-event-value %)])}])

(defn- form-group [key pre-label]
  (let [text (subscribe [:page :session-form :fields key])]
    [:div.form-group
     [label pre-label text]
     [input key text]]))

(defn- submit [event]
  (.preventDefault event)
  #_(dispatch [:submit-session-form]))

(defn edit-form []
  [:form {:on-submit submit}
   [form-group :start "Start: "]
   [form-group :finish "Finish: "]
   [:div.btn-toolbar
    [:button.btn.btn-primary {:type "submit"} "Update"]
    [:button.btn.btn-danger
     {:type     "button"
      #_:on-click #_(dispatch [:close-session-form])}
     "Cancel"]]])


