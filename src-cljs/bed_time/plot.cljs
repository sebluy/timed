(ns bed-time.plot
  (:require [cljs.core.async :refer [<! chan close! timeout]]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [bed-time.days :as days]
            [bed-time.util :as util])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce loading-chan (chan))

(defn init []
  (.load js/google "visualization" "1.0"
         (clj->js {:packages [:corechart]}))
  (.setOnLoadCallback js/google #(close! loading-chan)))

(defn time-slept []
  (let [pairs (fn [day] [(.toLocaleDateString (first day))
                         (-> day days/time-slept util/hours)])]
    (->> @days/days (filter days/valid?) (map pairs) reverse)))

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
    (doto (google.visualization.LineChart. (dom/getElement "plot-div"))
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

