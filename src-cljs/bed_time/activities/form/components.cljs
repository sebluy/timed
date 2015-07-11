(ns bed-time.activities.form.components
  (:require [bed-time.util :as util]
            [bed-time.activities.form.handlers :as form-handlers])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn- activity-input [field]
  [:input.form-control
   {:type        "text"
    :placeholder "Start Activity"
    :value       @field
    :on-change   #(form-handlers/update-field (util/get-event-value %))}])

(defn- submit-button [pending error]
  (cond
    @pending
    [:button.btn.btn-warning {:type "submit"} "Pending"]
    @error
    [:button.btn.btn-danger {:type "submit"} @error]
    :else
    [:button.btn.btn-success {:type "submit"} "Start"]))

(defn form []
  (with-subs [field [:page :activity-form :field]
              error [:page :activity-form :error]
              pending [:page :activity-form :pending]]
    (fn []
      [:form.navbar-form.navbar-right
       {:on-submit #(do (.preventDefault %)
                        (form-handlers/submit @field @error @pending))}
       [:div.form-group [activity-input field]]
       [submit-button pending error]])))
