(ns bed-time.core
  (:require [bed-time.navbar :as navbar]
            [bed-time.handlers :as handlers]
            [bed-time.subs :as subs]
            [bed-time.routing :as routing]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [re-frame.core :refer [subscribe dispatch]]))

(defn screen []
  [:div
   [navbar/navbar]
   [routing/current-page]])

(defn mount-components []
  (reagent/render-component [screen] (dom/getElement "app")))

(defn register-handlers-and-subs []
  (handlers/register)
  (subs/register))

(defn init! []
  (routing/hook-browser-navigation)
  (register-handlers-and-subs)
  (mount-components)
  (dispatch [:get-activities]))

