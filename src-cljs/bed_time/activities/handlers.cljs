(ns bed-time.activities.handlers
  (:require [re-frame.core :refer [register-handler dispatch]]
            [bed-time.activities.form.handlers :as form-handlers]
            [ajax.core :refer [POST GET]]
            [bed-time.activities.activities :as activities]))

(defn register []
  (register-handler
    :delete-activity
    (fn [db [_ activity]]
      (assoc db :activities (dissoc (db :activities) activity))))

  (register-handler
    :post-delete-activity
    (fn [db [_ activity]]
      (POST
        "/delete-activity"
        {:params          {:activity activity}
         :handler         #(dispatch [:delete-activity activity])
         :format          :edn
         :response-format :edn})
      db))

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
