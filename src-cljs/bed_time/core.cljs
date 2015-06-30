(ns bed-time.core
  (:require [bed-time.pages :as pages]
            [bed-time.framework.db :as db]
            [bed-time.machine :as machine]
            [bed-time.subs]
            [bed-time.routing :as routing]
            [reagent.core :as reagent]
            [goog.dom :as dom]))

(defn mount-components []
  (reagent/render-component [pages/view] (dom/getElement "app")))

(db/register-update-handler
  :set-page
  (fn [db page]
    (assoc db :page page)))

(defn init! []
  (db/run-update-render-machine)
  (machine/run)
  (routing/hook-browser-navigation)
  (mount-components))
;  (dispatch [:start-tick])
;  (dispatch [:get-activities]))

