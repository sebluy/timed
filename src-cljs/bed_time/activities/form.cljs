(ns bed-time.activities.form
  (:require [reagent.core :as reagent]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.util :as util]
            [bed-time.activities.activities :as activities]))

(defonce activity-field (reagent/atom nil))
(defonce error-field (reagent/atom nil))

(defn set-error []
  (reset! error-field "Activity cannot be blank"))

(defn reset-fields []
  (reset! activity-field nil)
  (reset! error-field nil))

(defn submit [event]
  (.preventDefault event)
  (let [activity @activity-field]
    (if (activities/valid? activity)
      (do (sessions/new-session activity)
          (reset-fields))
      (set-error))))

(defn activity-input []
  [:input {:type      "text"
           :class     "form-control"
           :value     @activity-field
           :on-change #(reset! activity-field (util/get-event-value %))}])

(defn error-alert []
  (if @error-field
    [:div.alert.alert-danger @error-field]))

(defn submit-button []
  [:button.btn.btn-primary {:type "submit"} "Start New Session"])

(defn form []
  [:form.form-horizontal {:on-submit submit}
   [:div.form-group
    [:label.col-sm-2.control-label "New Activity"]
    [:div.col-sm-7 [activity-input]]
    [:div.col-sm-3 [submit-button]]]
   [error-alert]])



