(ns bed-time.bed-times
  (:require [reagent.core :refer [atom]]
            [ajax.core :refer [GET POST]]))

(defn days-updater [days]
  (fn [response]
    (reset! days (into {} (map (fn [day]
                                 [(get day :date) day])
                               (response :days))))))

(defn get-days [days]
  (GET "/days" {:handler (days-updater days)
                :response-format :edn}))

(defn date [datetime]
  (let [date (js/Date. (.getTime datetime))]
    (.setHours date 0 0 0 0)
    date))

(defn go-to-bed-handler [days bed-time]
  (fn [response]
    (swap! days #(assoc-in % [(date bed-time) :bed_time] bed-time))))

(defn go-to-bed [days]
  (let [bed-time (js/Date.)]
    (POST "/go-to-bed" {:params {:bed-time bed-time}
                        :handler (go-to-bed-handler days bed-time)
                        :format :edn
                        :response-format :edn})))

(defn go-to-bed-button [days]
  [:input.btn.btn-large.btn-success
   {:type "button"
    :value "Go to bed!"
    :on-click #(go-to-bed days)}])

(defn wake-up-handler [days wake-up-time]
  (fn [response]
    (let [date (date wake-up-time)]
      (swap! days #(assoc % date {:date date
                                  :wake_up_time wake-up-time})))))

(defn wake-up [days]
  (let [wake-up-time (js/Date.)]
    (POST "/wake-up" {:params {:wake-up-time wake-up-time}
                      :handler (wake-up-handler days wake-up-time)
                      :format :edn
                      :response-format :edn})))

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

