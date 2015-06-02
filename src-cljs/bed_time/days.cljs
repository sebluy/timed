(ns bed-time.days
  (:require [reagent.core :as reagent]
            [ajax.core :as ajax]
            [cljs.core.async :refer [chan close!]]))

(defonce loading-chan (chan))

(defonce days
  (let [date-comparator (fn [day1 day2]
                          (> (.getTime day1) (.getTime day2)))]
    (reagent/atom (sorted-map-by date-comparator))))

(defn valid? [day]
  (not (nil? (second day))))

(defn time-slept [[bed-time wake-up-time]]
  (- (.getTime wake-up-time)
     (.getTime bed-time)))

(defn delete-day [[bed-time _ :as day]]
  (let [handler (fn [_] (swap! days #(dissoc % bed-time)))]
    (ajax/POST "/delete-day" {:params          {:day day}
                              :handler         handler
                              :format          :edn
                              :response-format :edn})))

(defn update-day [{:keys [bed-time wake-up-time] :as day}]
  (let [handler (fn [_] (swap! days #(assoc % bed-time wake-up-time)))]
    (ajax/POST "/update-day" {:params          {:day day}
                              :handler         handler
                              :format          :edn
                              :response-format :edn})))

(defn get-days []
  (println "Getting days...")
  (let [handler (fn [{incoming-days :days}]
                  (swap! days #(into % incoming-days))
                  (close! loading-chan))]
    (ajax/GET "/days" {:handler handler
                       :response-format :edn})))