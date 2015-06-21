(ns bed-time.activities.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v]]
            [bed-time.middleware :refer [static-db]]
            [bed-time.routing :as routing]
            [bed-time.activities.form.handlers :as form-handlers]
            [ajax.core :refer [POST GET]]
            [bed-time.activities.activities :as activities])
  (:import goog.History))

(defn- reset-activity-page-if-deleted [db activity]
  (when (= activity (get-in db [:page :route-params :activity]))
    (routing/redirect db {:handler :activities})))

(defn register []
  (register-handler
    :recieve-delete-activity
    trim-v
    (fn [db [activity]]
      (-> db
          (reset-activity-page-if-deleted activity)
          (update-in [:activities] #(dissoc % activity)))))

  (register-handler
    :delete-activity
    static-db
    (fn [[activity]]
      (POST
        "/delete-activity"
        {:params          {:activity activity}
         :handler         #(dispatch [:recieve-delete-activity activity])
         :format          :edn
         :response-format :edn})))

  (register-handler
    :recieve-activities
    (fn [db [_ incoming-activities]]
      (let [activities (activities/coerce-activities-to-sorted incoming-activities)]
        (merge db {:activities activities}))))

  (register-handler
    :get-activities
    (fn [db _]
      (GET "/activities"
           {:handler         #(dispatch [:recieve-activities %])
            :response-format :edn})
      db))

  (form-handlers/register))
