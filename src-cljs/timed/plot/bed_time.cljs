(ns timed.plot.timed
  (:require [goog.dom :as dom]
            [timed.plot.plot :as plot]
            [timed.util :as util]))

;(defn pair [[timed _]]
;  [(util/midnight timed) (util/time-of-day timed)])
;
;(defn timeds []
;  (->> @days/days (map pair)))
;
;(defn data-table []
;  (doto (google.visualization.DataTable.)
;    (.addColumn "date" "Date")
;    (.addColumn "timeofday" "Bed Time")
;    (.addRows (clj->js (timeds)))))
;
;(defn draw []
;  (let [data (data-table)
;        options {:title  "Bed Times"
;                 :legend {:position "none"}
;                 :height 450
;                 :vAxis  {:title      "Bed Time"}}]
;    (doto (google.visualization.LineChart. (dom/getElement "plot-div"))
;      (.draw data
;             (clj->js options)))))
;
;(defn page []
;  [plot/page draw])
