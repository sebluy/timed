(ns bed-time.header
  (:require [bed-time.days :as days]
            [bed-time.form :as form]
            [bed-time.state :as state]
            [bed-time.util :as util]))

(defn new-day-button []
  [:input.btn.btn-large.btn-primary.pull-right
   {:type     "button"
    :value    "New Day!"
    :on-click form/new-day}])

(defn tonights-bed-time []
  (let [current-days @days/days]
    (if-not (empty? current-days)
      (let [[most-recent-bed-time _] (first current-days)]
        (let [fifteen-minutes (* 1000 60 15)
              new-bed-time (js/Date. (- (.getTime most-recent-bed-time)
                                        fifteen-minutes))]
          (.toLocaleTimeString new-bed-time))))))

(defn avg-weekly-time-slept []
  (let [current-days (take 7 @days/days)]
    (util/hours-str (/ (reduce + (map days/time-slept current-days))
                       (count (filter days/valid? current-days))) 2)))

(defn days-info []
  [:div.col-md-6
   [:p "Tonights Bed Time: " (tonights-bed-time)]
   [:p "Average Weekly Time Slept: " (avg-weekly-time-slept) " Hours"]])

(defn header []
  [:div.page-header
   [:div.row
    [days-info]
    [:div.col-md-6 [new-day-button]]]
   (if (@state/state :update-form)
     [form/update-form])])

