(ns bed-time.days
  (:require [reagent.core :as reagent]
            [ajax.core :as ajax]
            [cljs.core.async :refer [chan close!]]))

(defonce loading-chan (chan))

(defn date-comparator [day1 day2]
  (> (.getTime day1) (.getTime day2)))

(defonce days (reagent/atom (sorted-map-by date-comparator)))

(defn update-handler [{:keys [bed-time wake-up-time]}]
  (fn [response]
    (swap! days #(assoc % bed-time wake-up-time))))

(defn update-day [day]
  (ajax/POST "/update-day" {:params {:day day}
                            :handler (update-handler day)
                            :format :edn
                            :response-format :edn}))

(defn get-days-handler [{incoming-days :days}]
  (swap! days #(into % incoming-days))
  (close! loading-chan))

(defn get-days []
  (ajax/GET "/days" {:handler get-days-handler
                     :response-format :edn}))

