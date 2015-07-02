(ns bed-time.activities.form.components
  (:require [bed-time.util :as util]
            [bed-time.activities.form.transitions :as transitions]
            [bed-time.framework.subscriptions :refer [subscribe]]
            [bed-time.framework.db :as db]
            [bed-time.sessions.handlers :as session-handlers])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn- activity-input [field]
  [:input {:type        "text"
           :class       "form-control"
           :placeholder "Activity name"
           :value       @field
           :on-change   #(db/transition (transitions/update-field
                                          (util/get-event-value %)))}])

(defn- error-alert [field error]
  (let [current-error @error]
    (if (and (not (nil? @field)) current-error)
      [:div.alert.alert-danger current-error])))

(defn- submit-button []
  [:button.btn.btn-primary {:type "submit"}
     "Start New Session"])

(defn- submit [event field error]
  (.preventDefault event)
  (when-not @error
    (session-handlers/start-session @field)))

(defn form []
  (let [field (subscribe [:page :activity-form :field])
        error (subscribe [:page :activity-form :error])]
    (fn []
      [:form.form-horizontal {:on-submit #(submit % field error)}
       [:div.form-group
        [:label.col-sm-4.control-label "New Activity"]
        [:div.col-sm-4 [activity-input field]]
        [:div.col-sm-4 [submit-button]]]
       [error-alert field error]])))

