(ns timed.plot.day
  (:require [goog.dom :as dom]
            [timed.util :as util]
            [timed.activities.activities :as activities]
            [timed.db :as db]))

;(defn pair [[timed _ :as day]]
;  [(util/midnight timed)
;   (-> day days/time-slept util/hours)])
;
;(defn time-slept []
;  (->> @days/days (filter days/valid?) (map pair) reverse))
;

(defn todays-sessions []
  (let [activities (db/query [:activities])]
    (reduce (fn [sessions activity]
              (into sessions (activities/sessions-from-last-n-days (activity :sessions) 1)))
            [] (vals activities))))

(defn activity-data [sessions]
  (map (fn [[_ {:keys [activity start finish]}]] [activity start finish])
       sessions))

(defn data-table []
  (doto (google.visualization.DataTable.)
    (.addColumn "string" "Activity")
    (.addColumn "date" "Start")
    (.addColumn "date" "Finish")
    (.addRows (clj->js (activity-data (todays-sessions))))))

(defn draw []
  (doto (google.visualization.Timeline. (dom/getElement "plot-div"))
    (.draw (data-table))))

