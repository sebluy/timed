(ns bed-time.framework.db
  (:require [reagent.core :as reagent]
            [cljs.core.async :refer [put! <! timeout chan]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [bed-time.macros :refer [go-forever]]))

(defonce db (reagent/atom {}))

(defonce update-chan (chan))

(defonce update-handlers (atom {}))

(defn- apply-update [[key args]]
  (swap! db #((@update-handlers key) % args)))

(defn register-update-handler [key fn]
  (swap! update-handlers #(assoc % key fn)))

(defn update-state [key args]
  (put! update-chan [key args]))

(defn run-update-render-machine []
  (go-forever
    (apply-update (<! update-chan))
    (<! (timeout 0))))

