(ns bed-time.core
  (:require [bed-time.pages.pages :as pages]
            [bed-time.pages.subs]
            [bed-time.pages.handlers :as handlers]
            [bed-time.navigation :as navigation]
            [reagent.core :as reagent]
            [goog.dom :as dom]))

(defn mount-components []
  (reagent/render-component [pages/view] (dom/getElement "app")))

(defn init! []
  (navigation/hook-browser)
  (mount-components)
  (handlers/get-activities))


