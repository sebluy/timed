(ns timed.plot.wake-up-time
  (:require [goog.dom :as dom]
            [timed.plot.plot :as plot]
            [timed.util :as util]))

;(defn pair [[_ wake-up-time]]
;  [(util/midnight wake-up-time) (util/time-of-day wake-up-time)])
;
;(defn timeds []
;  (->> @days/days (filter days/valid?) (map pair)))
;
;(defn data-table []
;  (doto (google.visualization.DataTable.)
;    (.addColumn "date" "Date")
;    (.addColumn "timeofday" "Wake Up Time")
;    (.addRows (clj->js (timeds)))))
;
;(defn draw []
;  (let [data (data-table)
;        options {:title  "Wake Up Times"
;                 :legend {:position "none"}
;                 :height 450
;                 :vAxis  {:title "Wake Up Time"}}]
;    (doto (google.visualization.LineChart. (dom/getElement "plot-div"))
;      (.draw data
;             (clj->js options)))))
;
;(defn page []
;  [plot/page draw])
