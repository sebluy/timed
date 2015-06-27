(ns bed-time.core
  (:require [bed-time.pages :as pages]
            [bed-time.subs]
            [bed-time.routing :as routing]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [re-frame.core :refer [dispatch dispatch-sync]]))

(defn mount-components []
  (reagent/render-component [pages/view] (dom/getElement "app")))

(defn init! []
  (routing/hook-browser-navigation)
  (mount-components))
;  (dispatch [:start-tick])
;  (dispatch [:get-activities]))

