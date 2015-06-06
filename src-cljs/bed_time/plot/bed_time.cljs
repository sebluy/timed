(ns bed-time.plot.bed-time
  (:require [goog.dom :as dom]
            [bed-time.days :as days]
            [bed-time.plot.plot :as plot]
            [bed-time.util :as util]))

(defn pair [[bed-time _]]
  [(util/midnight bed-time) (util/time-of-day bed-time)])

(defn bed-times []
  (->> @days/days (map pair)))

(defn data-table []
  (doto (google.visualization.DataTable.)
    (.addColumn "date" "Date")
    (.addColumn "timeofday" "Bed Time")
    (.addRows (clj->js (bed-times)))))

(defn draw []
  (let [data (data-table)
        options {:title  "Bed Times"
                 :legend {:position "none"}
                 :height 450
                 :vAxis  {:title      "Bed Time"}}]
    (doto (google.visualization.LineChart. (dom/getElement "plot-div"))
      (.draw data
             (clj->js options)))))

(defn page []
  [plot/page draw])
