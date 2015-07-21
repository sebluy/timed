(ns timed.core
  (:require [timed.pages.pages :as pages]
            [timed.pages.subs]
            [timed.local-storage :as storage]
            [timed.pages.handlers :as handlers]
            [timed.navigation :as navigation]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [timed.framework.db :as db]))

(defn mount-components []
  (reagent/render-component [pages/view] (dom/getElement "app")))

(defn init! []
  (storage/load-db)
  (navigation/hook-browser)
  (mount-components))

