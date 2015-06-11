(ns bed-time.plot.plot
  (:require [cljs.core.async :refer [<! chan close! timeout]]
            [reagent.core :as reagent])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;(defonce loading-chan (chan))
;
;(defn init []
;  (.load js/google "visualization" "1.0"
;         (clj->js {:packages [:corechart]}))
;  (.setOnLoadCallback js/google #(close! loading-chan)))
;
;(defn plot-div []
;  [:div#plot-div])
;
;(defn load-plot [draw-fn]
;  (go (<! loading-chan)
;      (<! days/loading-chan)
;      (draw-fn)
;      (add-watch days/days :plot draw-fn)))
;
;(defn plot [draw-fn]
;  (reagent/create-class
;    {:reagent-render      plot-div
;     :component-did-mount #(load-plot draw-fn)
;     :component-will-unmount #(remove-watch days/days :plot)}))
;
;(defn page [draw-fn]
;  (page/page [plot draw-fn]))
