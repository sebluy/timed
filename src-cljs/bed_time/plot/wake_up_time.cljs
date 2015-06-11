(ns bed-time.plot.wake-up-time
  (:require [goog.dom :as dom]
            [bed-time.plot.plot :as plot]
            [bed-time.util :as util]))

;(defn pair [[_ wake-up-time]]
;  [(util/midnight wake-up-time) (util/time-of-day wake-up-time)])
;
;(defn bed-times []
;  (->> @days/days (filter days/valid?) (map pair)))
;
;(defn data-table []
;  (doto (google.visualization.DataTable.)
;    (.addColumn "date" "Date")
;    (.addColumn "timeofday" "Wake Up Time")
;    (.addRows (clj->js (bed-times)))))
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
