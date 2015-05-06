(ns bed-time.bed-times
  (:require [reagent.core :refer [atom]]
            [ajax.core :refer [GET POST]]))

(defn bed-times-updater [bed-times]
  (fn [response]
    (reset! bed-times (into [] (map (fn [time] (time :time))
                                    (response :bed-times))))))

(defn get-bed-times [bed-times]
  (println "getting bed-times...")
  (GET "/bed-times" {:handler (bed-times-updater bed-times)
                     :response-format :edn}))

(defn go-to-bed-button [bed-times]
  [:input.btn.btn-large.btn-success
   {:type "button"
    :value "Go to bed!"
    :on-click (fn [] (swap!
                       bed-times
                       #(conj % (js/Date.))))}])

(defn remove-at-index [v n]
  (vec (concat (subvec v 0 n) (subvec v (inc n) (count v)))))

(defn delete-button [n bed-times]
  [:input.btn.btn-sm.btn-danger
   {:type "button"
    :value "Delete!"
    :on-click (fn [] (swap! bed-times #(remove-at-index % n)))}])

(defn show-bed-time [bed-times current-bed-times n]
  (let [bed-time (get current-bed-times n)]
    ^{:key n}
    [:tr
     [:td (.toLocaleDateString bed-time)]
     [:td (.toLocaleTimeString bed-time)]
     [:td (delete-button n bed-times)]]))

(defn bed-time-list [bed-times]
  [:table.table
   [:thead [:tr [:td "Date"] [:td "Time"]]]
   [:tbody
    (let [current-bed-times @bed-times]
      (for [n (range (count current-bed-times))]
        (show-bed-time bed-times current-bed-times n)))]])

(defn tonights-bed-time [bed-times]
  (let [current-bed-times @bed-times]
    (if-not (empty? current-bed-times)
      (let [last-bed-time (last current-bed-times)
            fifteen-minutes (* 1000 60 15)
            new-bed-time (js/Date. (- (.getTime last-bed-time)
                                      fifteen-minutes))]
        (.toLocaleTimeString new-bed-time)))))

(defn header [bed-times]
  [:div.page-header
   [:h1 "Bed Times"]
   [:h2 "Tonight: " (tonights-bed-time bed-times)
    [:div.pull-right
     (go-to-bed-button bed-times)]]])

(defn bed-times-page []
  (let [bed-times (atom [])]
    (get-bed-times bed-times)
    (fn []
      [:div.col-md-6.col-md-offset-3
       (header bed-times)
       (bed-time-list bed-times)])))

