(ns timed.pages.today
  (:require [timed.plot.plot :as plot]
            [timed.plot.day :as day]))

(defn page []
  [:div
   [:div.page-header
    [:h1 "Today"]]
   [plot/plot day/draw]])


