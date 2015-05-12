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

(defn todays-date []
  (let [today (js/Date.)]
    (.setHours today 0 0 0 0)
    today))

(defn go-to-bed-handler [days]
  (fn [response]
    (println "Response handled")))
;   (let [new-bed-time (response :bed-time)]
;     (if-not (nil? new-bed-time)
;       (let [date (todays-date)]
;         (swap! days #(assoc-in % [date :bed-time] new-bed-time)))))))

(defn go-to-bed [days]
  (println "Posting /go-to-bed")
  (POST "/go-to-bed" {:handler (go-to-bed-handler days)
                     :response-format :edn}))

(defn go-to-bed-button [days]
  [:input.btn.btn-large.btn-success
   {:type "button"
    :value "Go to bed!"
    :on-click #(go-to-bed days)}])

(defn wake-up-handler [days]
  (fn [response]
    (println "Response recieved")))
;   (let [new-wake-up-time (response :wake-up-time)]
;     (if-not (nil? new-wake-up-time)
;       (let [date (todays-date)]
;         (swap! days #(assoc % date {:date date
;                                     :wake_up_time new-wake-up-time})))))))

(defn wake-up [days]
  (println "Posting /wake-up")
  (POST "/wake-up" {:handler (wake-up-handler days)
                    :response-format :edn}))

(defn wake-up-button [days]
  [:input.btn.btn-large.btn-info
   {:type "button"
    :value "Wake Up!"
    :on-click #(wake-up days)}])

(defn delete-handler [days date]
  (fn [response]
    (swap! days #(dissoc % date))))

(defn delete [days date]
  (println "Posting /delete-day")
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
  (let [date (day :date)
        wake-up-time (day :wake_up_time)
        bed-time (day :bed_time)]
    ^{:key date}
    [:tr
     [:td (.toLocaleDateString date)]
     [:td (.toLocaleTimeString wake-up-time)]
     [:td (if-not (nil? bed-time) (.toLocaleTimeString bed-time))]
     [:td (delete-button days date)]]))

(defn day-list [days]
  [:table.table
   [:thead [:tr [:td "Date"] [:td "Wake Up Time"] [:td "Bed Time"]]]
   [:tbody
    (for [day (vals @days)]
      (show-day days day))]])

(defn tonights-bed-time [days]
  (let [current-days @days]
    (if-not (empty? current-days)
      (let [most-recent-bed-time ((first (vals current-days)) :bed_time)]
        (if-not (nil? most-recent-bed-time)
          (let [fifteen-minutes (* 1000 60 15)
                new-bed-time (js/Date. (- (.getTime most-recent-bed-time)
                                          fifteen-minutes))]
            (.toLocaleTimeString new-bed-time)))))))

(defn header [days]
   [:h2 "Tonight: " (tonights-bed-time days)
    [:div.pull-right
     (wake-up-button days)
     (go-to-bed-button days)]])

(defn bed-times-page []
  (let [days (atom [])]
    (get-days days)
    (fn []
      [:div.col-md-6.col-md-offset-3
       (header days)
       (day-list days)])))

