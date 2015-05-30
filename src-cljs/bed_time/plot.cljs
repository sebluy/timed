(ns bed-time.plot
  (:require [cljs.core.async :refer [<! chan close! timeout]]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [bed-time.days :as days])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce loading-chan (chan))

(defn init []
  (.load js/google "visualization" "1.0"
         (clj->js {:packages [:corechart :bar]}))
  (.setOnLoadCallback js/google #(close! loading-chan)))

(defn time-slept []
  (reverse (map (fn [[bed-time wake-up-time]]
                  [(.toLocaleDateString bed-time)
                   (/ (- (.getTime wake-up-time)
                         (.getTime bed-time))
                      3600000.0)])
                (filter #(not (nil? (second %))) @days/days))))

(defn time-slept-data-table []
  (doto (google.visualization.DataTable.)
    (.addColumn "string" "Date")
    (.addColumn "number" "Time Slept")
    (.addRows (clj->js (time-slept)))))
                             
(defn draw-plot []
  (let [data (time-slept-data-table)
        options {:title "Daily Amount of Time Slept"
                 :legend {:position "none"}
                 :height 450
                 :vAxis {:title "Time Slept (hours)"
                         :viewWindow {:min 0}}}]
    (doto (google.visualization.ColumnChart. (dom/getElement "plot-div"))
      (.draw data
             (clj->js options)))))

(defn plot-div []
  [:div#plot-div])

(defn load-plot []
  (go (<! loading-chan)
      (<! days/loading-chan)
      (draw-plot)
      (add-watch days/days :plot draw-plot)))

(defn plot []
  (reagent/create-class
    {:reagent-render plot-div
     :component-did-mount load-plot}))

