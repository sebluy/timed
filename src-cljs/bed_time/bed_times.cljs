(ns bed-time.bed-times
  (:require [reagent.core :refer [atom]]
            [ajax.core :refer [GET POST]]))

(defn days-updater [days]
  (fn [response]
    (reset! days (into {} (map (fn [day]
                                 [(get day :date) day])
                               (response :days))))))

(defn get-days [days]
  (println "getting days....")
  (GET "/days" {:handler (days-updater days)
                :response-format :edn}))

(defn go-to-bed-handler [bed-times]
  (fn [response]
    (let [new-bed-time (response :new-bed-time)]
      (if-not (nil? new-bed-time)
        (swap! bed-times #(into [new-bed-time] %))))))

(defn go-to-bed [bed-times]
  (POST "/go-to-bed" {:handler (go-to-bed-handler bed-times)
                      :response-format :edn}))

(defn go-to-bed-button [bed-times]
  [:input.btn.btn-large.btn-success
   {:type "button"
    :value "Go to bed!"
    :on-click #(go-to-bed bed-times)}])

(defn delete-handler [days date]
  (fn [response]
    (swap! days #(dissoc % date))))

(defn delete [days date]
  (POST "/delete-day" {:params {:date date}
                       :handler (delete-handler days date)
                       :format :edn
                       :response-format :edn}))

(defn delete-button [days day]
  [:input.btn.btn-sm.btn-danger
   {:type "button"
    :value "Delete!"
    :on-click #(delete days day)}])

(defn show-day [days day]
  (let [date (day :date)]
    ^{:key date}
    [:tr
     [:td (.toLocaleDateString date)]
     [:td (.toLocaleTimeString (day :wake_up_time))]
     [:td (.toLocaleTimeString (day :bed_time))]
     [:td (delete-button days date)]]))

(defn day-list [days]
  [:table.table
   [:thead [:tr [:td "Date"] [:td "Wake Up Time"] [:td "Bed Time"]]]
   [:tbody
    (for [day (vals @days)]
      (show-day days day))]])

(defn tonights-bed-time [bed-times]
  (let [current-bed-times @bed-times]
    (if-not (empty? current-bed-times)
      (let [last-bed-time (last current-bed-times)
            fifteen-minutes (* 1000 60 15)
            new-bed-time (js/Date. (- (.getTime last-bed-time)
                                      fifteen-minutes))]
        (.toLocaleTimeString new-bed-time)))))

(defn header [bed-times]
   [:h2 "Tonight: " (tonights-bed-time bed-times)
    [:div.pull-right
     (go-to-bed-button bed-times)]])

(defn bed-times-page []
  (let [days (atom [])]
    (get-days days)
    (fn []
      [:div.col-md-6.col-md-offset-3
 ;      (header bed-times)
       (day-list days)])))

