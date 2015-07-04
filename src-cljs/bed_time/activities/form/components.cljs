(ns bed-time.activities.form.components
  (:require [bed-time.util :as util]
            [bed-time.activities.form.transitions :as transitions]
            [bed-time.framework.db :as db]
            [bed-time.sessions.handlers :as session-handlers]))

(defn- activity-input [field]
  [:input.form-control
   {:type        "text"
    :placeholder "Start Activity"
    :value       @field
    :on-change   #(db/transition (transitions/update-field
                                   (util/get-event-value %)))}])

(defn- submit-button [field pending error]
  (cond
    @error
    [:button.btn.btn-danger {:type "submit"} @error]
    (and @pending (= @pending @field))
    [:button.btn.btn-warning {:type "submit"} "Pending"]
    :else
    [:button.btn.btn-success {:type "submit"} "Start"]))

(defn- submit [event field error pending]
  (.preventDefault event)
  (when-not (or @error @pending)
    (session-handlers/start-session @field)))

(defn form []
  (let [field (db/subscribe [:page :activity-form :field])
        error (db/subscribe [:page :activity-form :error])
        pending (db/subscribe [:pending :start-session])]
    (fn []
      [:form.navbar-form.navbar-right
       {:on-submit #(submit % field error pending)}
       [:div.form-group [activity-input field]]
       [submit-button field pending error]])))

