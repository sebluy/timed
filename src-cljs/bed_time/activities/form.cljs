(ns bed-time.activities.form
  (:require [reagent.core :as reagent]
            [clojure.string :as string]
            [bed-time.sessions.sessions :as sessions]))

(defonce activity-field (reagent/atom nil))
(defonce error-field (reagent/atom nil))

(defn get-event-value [event]
  (-> event .-target .-value))

(defn update-activity-field [event]
  (reset! activity-field (get-event-value event)))

(defn new-session [activity]
  {:activity activity :start (js/Date.) :finish nil :new true})

(defn valid? [activity]
  (not (string/blank? activity)))

(defn set-error []
  (reset! error-field "Activity cannot be blank"))

(defn submit [event]
  (.preventDefault event)
  (let [activity @activity-field]
    (if (valid? activity)
      (do (sessions/update-session (new-session @activity-field))
          (reset! activity-field nil)
          (reset! error-field nil))
      (set-error))))

(defn activity-input []
  [:input {:type      "text"
           :class     "form-control"
           :value     @activity-field
           :on-change update-activity-field}])

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



