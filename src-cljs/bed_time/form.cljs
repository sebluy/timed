(ns bed-time.form
  (:require [bed-time.days :as days]
            [reagent.core :refer [atom]]
            [ajax.core :refer [POST]]))

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
  (let [value-fn #(parse-datetime-str %)
        error-fn #(if (datetime-invalid? %2) "Invalid Time")]
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

(def datetime-input
  (let [label-fn #(some-> %2 .toLocaleString)]
    (fn [field pre-label]
      (text-input field pre-label label-fn update-time-field))))

(defn day-form-valid? [day]
  (every? #(and (not (get % :error)) (get % :value)) (vals day)))

(defn update-day [day-form]
  (let [current-day-form
        (into {} (map #(update-in % [1] deref) day-form))]
    (if (day-form-valid? current-day-form)
      (let [{:keys [wake-up-time bed-time] :as current-day}
            (into {} (map #(update-in % [1] :value) current-day-form))]
        (if (get @days/days bed-time)
          (days/update-day current-day)
          (days/update-day (merge {:new true} current-day)))))))

(defn update-form []
  (let [{:keys [bed-time wake-up-time] :as day}
        (into {} (for [field [:bed-time :wake-up-time]]
                   [field (atom {})]))]
    [:form
     [datetime-input bed-time "Bed Time: "]
     [datetime-input wake-up-time "Wake Up Time: "]
     [:input.btn.btn-primary
      {:type "button"
       :value "Update"
       :on-click #(update-day day)}]]))

