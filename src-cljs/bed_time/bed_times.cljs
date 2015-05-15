(ns bed-time.bed-times
  (:require [reagent.core :refer [atom]]
            [ajax.core :refer [GET POST]]))

(def days (atom {}))

(defn days-updater []
  (fn [response]
    (reset! days (into {} (map (fn [day]
                                 [(get day :date) day])
                               (response :days))))))

(defn get-days []
  (GET "/days" {:handler (days-updater)
                :response-format :edn}))

(defn date [datetime]
  (let [date (js/Date. (.getTime datetime))]
    (.setHours date 0 0 0 0)
    date))

(defn go-to-bed-handler [bed-time]
  (fn [response]
    (swap! days #(assoc-in % [(date bed-time) :bed-time] bed-time))))

(defn go-to-bed []
  (let [bed-time (js/Date.)]
    (POST "/go-to-bed" {:params {:bed-time bed-time}
                        :handler (go-to-bed-handler bed-time)
                        :format :edn
                        :response-format :edn})))

(defn go-to-bed-button []
  [:input.btn.btn-large.btn-success
   {:type "button"
    :value "Go to bed!"
    :on-click #(go-to-bed)}])

(defn wake-up-handler [wake-up-time]
  (fn [response]
    (let [date (date wake-up-time)]
      (swap! days #(assoc % date {:date date
                                  :wake-up-time wake-up-time})))))

(defn wake-up []
  (let [wake-up-time (js/Date.)]
    (POST "/wake-up" {:params {:wake-up-time wake-up-time}
                      :handler (wake-up-handler wake-up-time)
                      :format :edn
                      :response-format :edn})))

(defn wake-up-button []
  [:input.btn.btn-large.btn-info
   {:type "button"
    :value "Wake Up!"
    :on-click #(wake-up)}])

(defn delete-handler [date]
  (fn [response]
    (swap! days #(dissoc % date))))

(defn delete [date]
  (POST "/delete-day" {:params {:date date}
                       :handler (delete-handler date)
                       :format :edn
                       :response-format :edn}))

(defn delete-button [day]
  [:input.btn.btn-sm.btn-danger
   {:type "button"
    :value "Delete!"
    :on-click #(delete day)}])

(defn show-day [day]
  (let [date (day :date)
        wake-up-time (day :wake-up-time)
        bed-time (day :bed-time)]
    ^{:key date}
    [:tr
     [:td (.toLocaleDateString date)]
     [:td (.toLocaleTimeString wake-up-time)]
     [:td (if-not (nil? bed-time) (.toLocaleTimeString bed-time))]
     [:td (delete-button date)]]))

(defn day-list []
  [:table.table
   [:thead [:tr [:td "Date"] [:td "Wake Up Time"] [:td "Bed Time"]]]
   [:tbody
    (for [day (vals @days)]
      (show-day day))]])

(defn tonights-bed-time []
  (let [current-days @days]
    (if-not (empty? current-days)
      (let [most-recent-bed-time ((first (vals current-days)) :bed-time)]
        (if-not (nil? most-recent-bed-time)
          (let [fifteen-minutes (* 1000 60 15)
                new-bed-time (js/Date. (- (.getTime most-recent-bed-time)
                                          fifteen-minutes))]
            (.toLocaleTimeString new-bed-time)))))))

(defn header []
  [:h2 "Tonight: " (tonights-bed-time)
   [:div.pull-right
    (let [today (get @days (date (js/Date.)))]
      (if (nil? today)
        (wake-up-button)
        (if (nil? (today :bed-time))
          (go-to-bed-button))))]])

(defn update-date-map [element date-map]
  (let [date-str (-> element .-target .-value)
        date-val (js/Date. (.parse js/Date date-str))]
    (reset! date-map {:value date-val
                      :string date-str})))

(defn locale-date-string-if-not-nil [date]
  (if-not (nil? date)
    (.toLocaleDateString date)))

(defn date-input [date-map]
  (let [current-date-map @date-map]
    [:div.form-group
     [:label "Date: " (locale-date-string-if-not-nil
                        (current-date-map :value))]
     [:input {:type "text"
              :class "form-control"
              :value (current-date-map :string)
              :on-change #(update-date-map % date-map)}]]))

(defn str->date->str [date-str]
  (.toLocaleDateString (js/Date. (.parse js/Date date-str))))

(defn string->time->string [date-string time-string]
  (if-not (nil? time-string)
    (.toLocaleTimeString
      (js/Date. (.parse js/Date (str @date-string " " time-string))))))

(defn update-bed-time [date-string wake-up-time-string bed-time-string]
  (let [date (js/Date. (.parse js/Date @date-string))
        wake-up-time (js/Date. (.parse js/Date @wake-up-time-string))
        bed-time (js/Date. (.parse js/Date @bed-time-string))]
    (swap! days #(assoc % date {:date date
                                :wake-up-time wake-up-time
                                :bed-time bed-time}))))

(defn update-bed-time-form []
  (let [date (atom {})
        wake-up-time (atom {})
        bed-time (atom {})]
    [:form
     [date-input date]
     [:input.btn.btn-primary
      {:type "button"
       :value "Add"
       :on-click #(update-bed-time date wake-up-time bed-time)}]]))

(defn page-header [element]
  [:div.page-header
    [element]])

(defn bed-times-page []
  (get-days)
  (fn []
    [:div.col-md-6.col-md-offset-3
     [page-header header]
     [page-header update-bed-time-form]
     [day-list]]))

