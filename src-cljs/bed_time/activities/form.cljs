(ns bed-time.activities.form
  (:require [reagent.core :as reagent]
            [clojure.string :as string]
            [bed-time.activities.session :as session]))

(defonce activity (reagent/atom nil))

(defn get-event-value [event]
  (-> event .-target .-value))

(defn update-activity [event]
  (reset! activity (get-event-value event)))

(defn input []
  [:div.form-group
   [:label "New Activity"]
   [:input {:type      "text"
            :class     "form-control"
            :value     @activity
            :on-change update-activity}]])

(defn form []
  (let [click-handler (fn []
                        (if (not (string/blank? @activity))
                          (session/update-session {:activity @activity
                                                   :start    (js/Date.)
                                                   :finish   nil
                                                   :new      true})))]
    [:form
     [input]
     [:input.btn.btn-primary
      {:type     "button"
       :value    "Start New Session"
       :on-click click-handler}]]))

