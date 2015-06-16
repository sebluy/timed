(ns bed-time.activities.form
  (:require [reagent.core :as reagent]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.util :as util]
            [bed-time.activities.activities :as activities]
            [re-frame.core :as re-frame])
  (:require-macros [reagent.ratom :as reaction]))

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
      (do (reset-fields)
          (re-frame/dispatch [:new-session])))))

(re-frame/register-handler
  :update-activity-form
  (fn [db [_ activity]]
    (assoc-in db [:activity-form :field] activity)))

(re-frame/register-handler
  :submit-activity-form
  (fn [db _]
    (let [activity (get-in db [:activity-form :field])]
      (if-let [error (activities/error activity)]
        (assoc-in db [:activity-form :error] error)
        (do (re-frame/dispatch [:new-session activity])
            (assoc db :activity-form {:field nil :error nil}))))))

(re-frame/register-sub
  :activity-form-error
  (fn [db _]
    (reaction/reaction (get-in @db [:activity-form :error]))))

(re-frame/register-sub
  :activity-form-field
  (fn [db _]
    (reaction/reaction (get-in @db [:activity-form :field]))))

(defn activity-input []
  (let [activity-field (re-frame/subscribe [:activity-form-field])]
    [:input {:type        "text"
             :class       "form-control"
             :placeholder "Activity name"
             :value       @activity-field
             :on-change   #(re-frame/dispatch
                            [:update-activity-form
                             (util/get-event-value %)])}]))

(defn error-alert []
  (let [error (re-frame/subscribe [:activity-form-error])]
    (fn []
      (if @error
        [:div.alert.alert-danger @error]))))

(defn submit-button []
  [:button.btn.btn-primary {:type "submit"} "Start New Session"])

(defn form []
  [:form.form-horizontal
   {:on-submit #(do (.preventDefault %)
                    (re-frame/dispatch [:submit-activity-form]))}
   [:div.form-group
    [:label.col-sm-4.control-label "New Activity"]
    [:div.col-sm-4 [activity-input]]
    [:div.col-sm-4 [submit-button]]]
   [error-alert]])

