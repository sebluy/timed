(ns timed.activities.form.components
  (:require [timed.util :as util]
            [timed.pages.components :as page-components]
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
  (with-subs
    [status [:page :activity-form :status]]
    (fn []
      (if (= @status :hidden)
        nil
        [:form.navbar-form
         {:on-submit #(do (.preventDefault %) (form-handlers/submit))}
         [:div.form-group [activity-input]]
         [submit-button]]))))

