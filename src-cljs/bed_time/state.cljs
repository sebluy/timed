(ns bed-time.state
  (:require [reagent.core :as reagent]))

(defonce state (reagent/atom {:update-form false}))

(defonce current-session (reagent/atom nil))

