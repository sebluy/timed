(ns bed-time.activities.form.components
  (:require [bed-time.util :as util]
            [bed-time.framework.subscriptions :refer [subscribe]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn- activity-input [field]
  [:input {:type        "text"
           :class       "form-control"
           :placeholder "Activity name"
           :value       @field
           #_:on-change  #_(dispatch-sync
                          [:update-activity-form
                           (util/get-event-value %)])}])

(defn- error-alert [field error]
  (let [current-error @error]
    (if (and (not (nil? @field)) current-error)
      [:div.alert.alert-danger current-error])))

(defn- submit-button [pending]
  [:button.btn.btn-primary {:type "submit"}
   (if @pending
     "Pending"
     "Start New Session")])

(defn- submit [event field error pending]
  (.preventDefault event)
  (when-not (or @error @pending)
    #_(dispatch [:submit-activity-form @field])))

(defn form []
  (let [field (subscribe [:page :activity-form :field])
        error (subscribe [:page :activity-form :error])
        pending (subscribe [:page :activity-form :pending])]
    (fn []
      [:form.form-horizontal {:on-submit #(submit % field error pending)}
       [:div.form-group
        [:label.col-sm-4.control-label "New Activity"]
        [:div.col-sm-4 [activity-input field]]
        [:div.col-sm-4 [submit-button pending]]]
       [error-alert field error]])))

