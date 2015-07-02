(ns bed-time.core
  (:require [bed-time.pages :as pages]
            [bed-time.subs]
            [bed-time.handlers :as handlers]
            [bed-time.history :as history]
            [reagent.core :as reagent]
            [goog.dom :as dom]))

(defn mount-components []
  (reagent/render-component [pages/view] (dom/getElement "app")))

(defn init! []
  (history/hook-browser)
  (mount-components)
  (handlers/get-activities))

