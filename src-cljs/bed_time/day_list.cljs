(ns bed-time.day-list
  (:require [ajax.core :as ajax]
            [bed-time.days :as days]
            [goog.string :as string]))


(defn delete-handler [[bed-time _]]
  (fn [response]
    (swap! days/days #(dissoc % bed-time))))

(defn delete-day [day]
  (ajax/POST "/delete-day" {:params {:day day}
                       :handler (delete-handler day)
                       :format :edn
                       :response-format :edn}))

(defn delete-day-button [day]
  [:input.btn.btn-sm.btn-danger
   {:type "button"
    :value "Delete!"
    :on-click #(delete-day day)}])

(defn time-slept [[bed-time wake-up-time]]
  (string/format "%.2f" (/ (- (.getTime wake-up-time)
                                   (.getTime bed-time))
                                3600000.0)))

(defn show-day [[bed-time wake-up-time :as day]]
  ^{:key (.getTime bed-time)}
  [:tr
   [:td (.toLocaleString bed-time)]
   [:td (some-> wake-up-time .toLocaleString)]
   [:td (if (and wake-up-time bed-time) (time-slept day))]
   [:td (delete-day-button day)]])

(defn day-list []
  [:table.table
   [:thead
    [:tr [:td "Bed Time"] [:td "Wake Up Time"] [:td "Time Slept (Hours)"]]]
   [:tbody
    (for [day @days/days]
      (show-day day))]])

