(ns bed-time.plot
  (:require [cljs.core.async :refer [<! chan close! timeout]]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [bed-time.days :refer [days]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce loading-chan (chan))

(defn init []
  (.load js/google "visualization" "1.0" (clj->js {:packages [:timeline]}))
  (.setOnLoadCallback js/google #(close! loading-chan)))

(defn days-data-table []
  (.arrayToDataTable js/google.visualization
                     (clj->js
                       (into [["Day" "Bed Time" "Wake Up Time"]]
                             (map (fn [[bed-time wake-up-time]]
                                    [(.toLocaleDateString bed-time)
                                     bed-time
                                     wake-up-time])
                                  @days)))))

(defn draw-plot []
  (let [data (days-data-table)
        options {:height 450}]
    (doto (google.visualization.Timeline. (dom/getElement "plot-div"))
      (.draw data
             (clj->js options)))))

(defn plot-div []
  [:div#plot-div])

(defn load-plot []
  (go (<! loading-chan)
      (draw-plot)))

(defn plot []
  (reagent/create-class
    {:reagent-render plot-div
     :component-did-mount load-plot}))

