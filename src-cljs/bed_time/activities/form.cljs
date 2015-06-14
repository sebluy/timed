(ns bed-time.activities.form
  (:require [reagent.core :as reagent]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.util :as util]
            [bed-time.activities.activities :as activities]))

(defonce activity-field (reagent/atom nil))
(defonce error-field (reagent/atom nil))

(defn set-error [error]
  (reset! error-field error))

(defn reset-fields []
  (reset! activity-field nil)
  (reset! error-field nil))

(defn submit [event]
  (.preventDefault event)
  (let [activity @activity-field]
    (if-let [error (activities/error activity)]
      (set-error error)
      (do (sessions/new-session activity)
          (reset-fields)))))

(defn activity-input []
  [:input {:type        "text"
           :class       "form-control"
           :placeholder "Activity name"
           :value       @activity-field
           :on-change   #(reset! activity-field (util/get-event-value %))}])

(defn error-alert []
  (if @error-field
    [:div.alert.alert-danger @error-field]))

(defn submit-button []
  [:button.btn.btn-primary {:type "submit"} "Start New Session"])

(defn form []
  [:form.form-horizontal {:on-submit submit}
   [:div.form-group
    [:label.col-sm-4.control-label "New Activity"]
    [:div.col-sm-4 [activity-input]]
    [:div.col-sm-4 [submit-button]]]
   [error-alert]])

