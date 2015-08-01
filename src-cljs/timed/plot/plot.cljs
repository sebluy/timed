(ns timed.plot.plot
  (:require [cljs.core.async :refer [<! chan close! timeout]]
            [reagent.core :as reagent])
  (:require-macros [cljs.core.async.macros :as async]))

(defonce loading-chan (chan))

(defn init []
  (.load js/google "visualization" "1.0"
         (clj->js {:packages [:corechart :timeline]}))
  (.setOnLoadCallback js/google #(close! loading-chan)))

(defn- plot-div []
  [:div#plot-div])

(defn- mount-plot [draw-fn]
  (async/go (<! loading-chan)
            (draw-fn)))

(defn- unmount-plot [])

(defn plot [draw-fn]
  (reagent/create-class
    {:reagent-render      plot-div
     :component-did-mount #(mount-plot draw-fn)
     :component-will-unmount unmount-plot}))
