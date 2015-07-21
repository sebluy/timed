(ns timed.core
  (:require [timed.pages.pages :as pages]
            [timed.pages.subs]
            [timed.local-storage]
            [timed.pages.handlers :as handlers]
            [timed.navigation :as navigation]
            [reagent.core :as reagent]
            [goog.dom :as dom]))

(defn mount-components []
  (reagent/render-component [pages/view] (dom/getElement "app")))

(defn init! []
  (handlers/go-online)
  (navigation/hook-browser)
  (mount-components)
  (handlers/get-activities))

