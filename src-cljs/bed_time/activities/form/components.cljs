(ns bed-time.activities.form.components
  (:require [bed-time.util :as util]
            [re-frame.core :refer [subscribe dispatch-sync dispatch]]))

(defn- activity-input []
  (let [activity-field (subscribe [:activity-form-field])]
    [:input {:type        "text"
             :class       "form-control"
             :placeholder "Activity name"
             :value       @activity-field
             :on-change   #(dispatch-sync
                            [:update-activity-form
                             (util/get-event-value %)])}]))

(defn- error-alert []
  (let [error (subscribe [:activity-form-error])]
    (fn []
      (if @error
        [:div.alert.alert-danger @error]))))

(defn- submit-button []
  [:button.btn.btn-primary {:type "submit"} "Start New Session"])

(defn- submit [event]
  (.preventDefault event)
  (dispatch [:submit-activity-form]))

(defn form []
  [:form.form-horizontal {:on-submit submit}
   [:div.form-group
    [:label.col-sm-4.control-label "New Activity"]
    [:div.col-sm-4 [activity-input]]
    [:div.col-sm-4 [submit-button]]]
   [error-alert]])

