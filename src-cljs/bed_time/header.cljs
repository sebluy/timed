(ns bed-time.header
  (:require [ajax.core :as ajax]
            [bed-time.days :as days]))

(defn go-to-bed []
  (days/update-day {:bed-time (js/Date.) :new true}))

(defn go-to-bed-button []
  [:input.btn.btn-large.btn-success
   {:type "button"
    :value "Go to bed!"
    :on-click #(go-to-bed)}])

(defn wake-up []
  (days/update-day {:bed-time (first (first @days/days))
               :wake-up-time (js/Date.)}))

(defn wake-up-button []
  [:input.btn.btn-large.btn-info
   {:type "button"
    :value "Wake Up!"
    :on-click #(wake-up)}])

(defn tonights-bed-time []
  (let [current-days @days/days]
    (if-not (empty? current-days)
      (let [[most-recent-bed-time _] (first current-days)]
        (let [fifteen-minutes (* 1000 60 15)
              new-bed-time (js/Date. (- (.getTime most-recent-bed-time)
                                        fifteen-minutes))]
          (.toLocaleTimeString new-bed-time))))))

(defn header []
  [:h2 "Tonight: " (tonights-bed-time)
   [:div.pull-right.btn-group
    (wake-up-button)
    (go-to-bed-button)]])

