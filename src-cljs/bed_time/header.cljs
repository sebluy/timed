(ns bed-time.header
  (:require [ajax.core :refer [POST]]
            [bed-time.days :refer [days]]))

(defn go-to-bed-handler [bed-time]
  (fn [response]
    (swap! days #(assoc % bed-time nil))))

(defn go-to-bed []
  (let [bed-time (js/Date.)]
    (POST "/add-day" {:params {:day {:bed-time bed-time}}
                      :handler (go-to-bed-handler bed-time)
                      :format :edn
                      :response-format :edn})))

(defn go-to-bed-button []
  [:input.btn.btn-large.btn-success
   {:type "button"
    :value "Go to bed!"
    :on-click #(go-to-bed)}])

(defn wake-up-handler [{:keys [bed-time wake-up-time]}]
  (fn [response]
    (swap! days #(assoc % bed-time wake-up-time))))

(defn wake-up []
  (let [[bed-time _] (first @days)
        wake-up-time (js/Date.)
        day {:bed-time bed-time :wake-up-time wake-up-time}]
    (POST "/update-day" {:params {:day day}
                         :handler (wake-up-handler day)
                         :format :edn
                         :response-format :edn})))

(defn wake-up-button []
  [:input.btn.btn-large.btn-info
   {:type "button"
    :value "Wake Up!"
    :on-click #(wake-up)}])

(defn tonights-bed-time []
  (let [current-days @days]
    (if-not (empty? current-days)
      (let [[most-recent-bed-time _] (first current-days)]
        (let [fifteen-minutes (* 1000 60 15)
              new-bed-time (js/Date. (- (.getTime most-recent-bed-time)
                                        fifteen-minutes))]
          (.toLocaleTimeString new-bed-time))))))

(defn header []
  [:h2 "Tonight: " (tonights-bed-time)
   [:div.pull-right
    (wake-up-button)
    (go-to-bed-button)]])

