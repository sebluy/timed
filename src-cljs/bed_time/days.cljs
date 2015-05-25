(ns bed-time.days
  (:require [reagent.core :refer [atom]]
            [ajax.core :refer [GET]]))

(defn date-comparator [day1 day2]
  (> (.getTime day1) (.getTime day2)))

(def days (atom (sorted-map-by date-comparator)))

(defn days-updater [{incoming-days :days}]
  (swap! days #(into % incoming-days)))

(defn get-days []
  (GET "/days" {:handler days-updater
                :response-format :edn}))

