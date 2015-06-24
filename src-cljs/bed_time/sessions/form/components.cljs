(ns bed-time.sessions.form.components
  (:require [re-frame.core :refer [dispatch dispatch-sync]]
            [bed-time.util :as util])
  (:require-macros [reagent.ratom :refer [reaction]]))

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
    :on-change #(dispatch-sync
                 [:change-session-form-field key (util/get-event-value %)])}])

(defn- form-group [key text pre-label]
  [:div.form-group
   [label pre-label text]
   [input key text]])

(defn- submit [event]
  (.preventDefault event)
  (dispatch [:submit-session-form]))

(defn edit-form [form-reaction]
  (let [start-text (reaction (get-in @form-reaction [:fields :start]))
        finish-text (reaction (get-in @form-reaction [:fields :finish]))]
    (fn []
      [:form {:on-submit submit}
       [form-group :start start-text "Start: "]
       [form-group :finish finish-text "Finish: "]
       [:div.btn-toolbar
        [:button.btn.btn-primary {:type "submit"} "Update"]
        [:button.btn.btn-danger
         {:type     "button"
          :on-click #(dispatch [:close-session-form])}
         "Cancel"]]])))


