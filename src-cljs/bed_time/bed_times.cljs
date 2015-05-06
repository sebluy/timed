(ns bed-time.bed-times
  (:require [reagent.core :refer [atom]]
            [ajax.core :refer [GET POST]]))

(defn bed-times-updater [bed-times]
  (fn [response]
    (reset! bed-times (into [] (map (fn [time]
                                      (.toUTCString (time :time)))
                                    (response :bed-times))))))

(defn get-bed-times [bed-times]
  (GET "/bed-times" {:handler (bed-times-updater bed-times)
                     :response-format :edn}))

(defn go-to-bed-button [bed-times]
  [:input {:type "button"
           :value "Go to bed!"
           :on-click (fn [] (swap!
                              bed-times
                              #(conj % (.toUTCString (js/Date.)))))}])

(defn remove-at-index [v n]
  (vec (concat (subvec v 0 n) (subvec v (inc n) (count v)))))

(defn delete-button [n bed-times]
  [:input {:type "button"
           :value "Delete!"
           :on-click (fn [] (swap! bed-times #(remove-at-index % n)))}])

(defn bed-time-list [bed-times]
  [:ul
   (let [current-bed-times @bed-times]
     (for [n (range (count current-bed-times))]
       ^{:key n} [:li
                  (get current-bed-times n)
                  (delete-button n bed-times)]))])

(defn bed-times-page []
  (let [bed-times (atom [])]
    (get-bed-times bed-times)
    (fn []
      [:div
       (go-to-bed-button bed-times)
       (bed-time-list bed-times)])))

