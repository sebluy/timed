(ns bed-time.bed-times
  (:require [reagent.core :refer [atom]]
            [ajax.core :refer [GET POST]]))


(defn date-comparator [day1 day2]
  (> (.getTime day1) (.getTime day2)))

(def days (atom (sorted-map-by date-comparator)))

(defn days-updater [response]
  (println (response :days))
  (swap! days #(into % (map (fn [day]
                              [(get day :date) day])
                            (response :days)))))

(defn get-days []
  (GET "/days" {:handler days-updater
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

(defn delete-button [date]
  [:input.btn.btn-sm.btn-danger
   {:type "button"
    :value "Delete!"
    :on-click #(delete date)}])

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

;;;; New day form

(defn parse-datetime-str [datetime-str]
  (->> datetime-str (.parse js/Date) js/Date.))

(defn get-event-value [event]
  (-> event .-target .-value))

(defn datetime-invalid? [datetime]
  (or (nil? datetime) (js/isNaN (.getTime datetime))))

(defn update-field [field event value-fn error-fn]
  (let [text (get-event-value event)
        value (value-fn text)
        error (error-fn text value)]
    (reset! field {:text text :value value :error error})))

(def update-time-field
  (let [default-date-str "1970-01-01"
        value-fn #(parse-datetime-str (str default-date-str " " %))
        error-fn #(if (or (empty? %1) (datetime-invalid? %2)) "Invalid Time")]
    (fn [field event]
      (update-field field event value-fn error-fn))))

(def update-date-field
  (let [value-fn #(parse-datetime-str %)
        error-fn #(if (datetime-invalid? %2) "Invalid Date")]
    (fn [field event]
      (update-field field event value-fn error-fn))))

(defn error-label [error alternate]
  (if error
    [:span.label.label-danger error]
    [:span.label.label-success alternate]))

(defn text-input [field pre-label label-fn update-fn]
  (let [{:keys [text value error]} @field
        label (label-fn text value error)]
    [:div.form-group
     [:label pre-label (error-label error label)]
     [:input {:type "text"
              :class "form-control"
              :value text
              :on-change #(update-fn field %)}]]))

(def date-input
  (let [pre-label "Date: "
        label-fn #(some-> %2 .toLocaleDateString)]
    (fn [field]
      (text-input field pre-label label-fn update-date-field))))
          
(def time-input
  (let [label-fn #(some-> %2 .toLocaleTimeString)]
    (fn [field pre-label]
      (text-input field pre-label label-fn update-time-field))))

(defn day-form-valid? [day]
  (every? #(and (not (get % :error)) (get % :value)) (vals day)))

(defn clean-day [day]
  (let [{:keys [date bed-time wake-up-time]} day
        year (.getFullYear date)
        month (.getMonth date)
        day (.getDate date)]
    {:date
     (doto date (.setHours 0) (.setMinutes 0) (.setSeconds 0))
     :wake-up-time
     (doto wake-up-time (.setFullYear year) (.setMonth month) (.setDate day))
     :bed-time
     (doto bed-time (.setFullYear year) (.setMonth month) (.setDate day))}))

(defn update-day [day-form]
  (let [current-day-form
        (into {} (map #(update-in % [1] deref) day-form))]
    (if (day-form-valid? current-day-form)
      (let [{:keys [date] :as current-day}
            (into {} (map #(update-in % [1] :value) current-day-form))]
        (println (clean-day current-day))))))
;        (swap! days #(assoc % date (clean-day current-day)))))))

(defn update-bed-time-form []
  (let [{:keys [date wake-up-time bed-time] :as day}
        (into {} (for [field [:date :wake-up-time :bed-time]]
                   [field (atom {})]))]
    [:form
     [date-input date]
     [time-input wake-up-time "Wake Up Time: "]
     [time-input bed-time "Bed Time: "]
     [:input.btn.btn-primary
      {:type "button"
       :value "Add"
       :on-click #(update-day day)}]]))

;;;; Top Level Layout

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

