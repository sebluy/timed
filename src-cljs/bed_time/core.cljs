(ns bed-time.core
  (:require [bed-time.pages :as pages]
            [bed-time.handlers]
            [bed-time.subs]
            [bed-time.routing :as routing]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [re-frame.core :refer [subscribe dispatch]]))

(defn mount-components []
  (reagent/render-component [pages/view] (dom/getElement "app")))

(defn init! []
  (routing/hook-browser-navigation)
  (mount-components)
  (dispatch [:get-activities]))

