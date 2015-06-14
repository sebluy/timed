(ns bed-time.activities.form
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.util :as util]
            [bed-time.activities.activities :as activities])
  (:require-macros [bed-time.macros :refer [go-forever]]
                   [cljs.core.async.macros :refer [go]]))

(defn go-activity-input [activity-field chans]
  (go-forever
    (async/<! (chans :request))
    (let [activity @activity-field
          error (activities/error activity)]
      (if-not error
        (do
          (async/>! (chans :activity) {:status :valid :activity activity})
          (async/>! (chans :error) false)
          (reset! activity-field nil))
        (do
          (async/>! (chans :activity) {:status :invalid})
          (async/>! (chans :error) error))))))

(defn go-error-field [error-field error-chan]
  (go-forever
    (reset! error-field (async/<! error-chan))))

(defn go-form [chans]
  (go-forever
    (async/<! (chans :submit))
    (async/>! (chans :request) :request)
    (let [activity-data (async/<! (chans :activity))]
      (if (= (activity-data :status) :valid)
        (sessions/new-session (activity-data :activity))))))

(defn activity-input [chans]
  (let [activity-field (reagent/atom nil)]
    (go-activity-input activity-field chans)
    (fn []
      [:input {:type      "text"
               :class     "form-control"
               :value     @activity-field
               :on-change #(reset! activity-field (util/get-event-value %))}])))

(defn error-alert [error-chan]
  (let [error-field (reagent/atom nil)]
    (fn []
      (go-error-field error-field error-chan)
      (if @error-field
        [:div.alert.alert-danger @error-field]))))

(defn submit-button []
  [:button.btn.btn-primary {:type "submit"} "Start New Session"])

(defn form []
  (let [chans {:submit (async/chan) :request (async/chan)
               :activity (async/chan) :error (async/chan)}]
    (fn []
      (go-form (select-keys chans [:submit :request :activity]))
      [:form.form-horizontal
       {:on-submit #(do (.preventDefault %)
                        (go (async/>! (chans :submit) :submit)))}
       [:div.form-group
        [:label.col-sm-2.control-label "New Activity"]
        [:div.col-sm-7
         [activity-input (select-keys chans [:request :activity :error])]]
        [:div.col-sm-3 [submit-button]]]
       [error-alert (chans :error)]])))

