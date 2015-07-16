(ns timed.plot.time-slept
  (:require [goog.dom :as dom]
            [timed.util :as util]))

;(defn pair [[timed _ :as day]]
;  [(util/midnight timed)
;   (-> day days/time-slept util/hours)])
;
;(defn time-slept []
;  (->> @days/days (filter days/valid?) (map pair) reverse))
;
;(defn data-table []
;  (doto (google.visualization.DataTable.)
;    (.addColumn "date" "Date")
;    (.addColumn "number" "Time Slept")
;    (.addRows (clj->js (time-slept)))))
;
;(defn draw []
;  (let [data (data-table)
;        options {:title  "Daily Amount of Time Slept"
;                 :legend {:position "none"}
;                 :height 450
;                 :vAxis  {:title      "Time Slept (hours)"}}]
;    (doto (google.visualization.LineChart. (dom/getElement "plot-div"))
;      (.draw data
;             (clj->js options)))))
;
;(defn page []
;  [plot/page draw])
