(ns bed-time.activities.session
  (:require [bed-time.activities.core :as activities]
            [ajax.core :as ajax]))

(defn delete [activity [start _ :as session]]
  (let [swap-fn (fn [activities]
                  (merge activities
                         {activity (dissoc (activities activity) start)}))
        handler (fn [_] (swap! activities/activities swap-fn))]
    (ajax/POST "/delete-session" {:params          {:session session}
                                  :handler         handler
                                  :format          :edn
                                  :response-format :edn})))

(defn valid? [[_ finish]]
  (not (nil? finish)))

(defn time-spent [[start finish :as session]]
  (if (valid? session)
    (- (.getTime finish)
       (.getTime start))
    0))

