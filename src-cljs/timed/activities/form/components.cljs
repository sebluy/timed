(ns timed.activities.form.components
  (:require [timed.util :as util]
            [timed.activities.form.handlers :as form-handlers])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn- activity-input [field]
  (with-subs
    [field [:page :activity-form :field]]
    (fn []
      [:input.form-control
       {:type        "text"
        :placeholder "Start Activity"
        :value       @field
        :on-change   #(form-handlers/update-field (util/get-event-value %))}])))

(defn- submit-button []
  (with-subs
    [error [:page :activity-form :error]]
    (fn []
      (if @error
        [:button.btn.btn-danger {:type "submit"} @error]
        [:button.btn.btn-success {:type "submit"} "Start"]))))

(defn form []
  [:form.navbar-form
   {:on-submit #(do (.preventDefault %) (form-handlers/submit))}
   [:div.form-group [activity-input]]
   [submit-button]])

