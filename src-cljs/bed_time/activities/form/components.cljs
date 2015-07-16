(ns bed-time.activities.form.components
  (:require [bed-time.util :as util]
            [bed-time.pages.components :as page-components]
            [bed-time.activities.form.handlers :as form-handlers])
  (:require-macros [bed-time.macros :refer [with-subs]]))

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
    [status [:page :activity-form :status]
     error [:page :activity-form :error]]
    (fn []
      (condp = @status
        :pending
        [page-components/pending-button]
        :error
        [:button.btn.btn-danger {:type "submit"} @error]
        [:button.btn.btn-success {:type "submit"} "Start"]))))

(defn form []
  [:form.navbar-form.navbar-right
   {:on-submit #(do (.preventDefault %) (form-handlers/submit))}
   [:div.form-group [activity-input]]
   [submit-button]])

