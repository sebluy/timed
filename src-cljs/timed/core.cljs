(ns timed.core
  (:require [timed.pages.pages :as pages]
            [timed.pages.subs]
            [timed.local-storage :as storage]
            [timed.navigation :as navigation]
            [timed.plot.plot :as plot]
            [reagent.core :as reagent]
            [goog.dom :as dom]))

(defn mount-components []
  (reagent/render-component [pages/view] (dom/getElement "app")))

(defn init! []
  (plot/init)
  (storage/load-db)
  (navigation/hook-browser)
  (mount-components))

