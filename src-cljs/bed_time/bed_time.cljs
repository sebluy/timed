(ns bed-time.bed-time
  (:require [bed-time.days :refer [days get-days]]
            [bed-time.day-list :refer [day-list]]
            [bed-time.form :refer [update-form]]
            [bed-time.header :refer [header]]
            [bed-time.plot :refer [plot]]
            [bed-time.state :as state]))

(defn section [element]
  [:div.page-header
   [element]])

(defn bed-time-page []
  (get-days)
  (fn []
    [:div.col-md-6.col-md-offset-3
     [section header]
     (if (@state/state :update-form)
       [section update-form])
     [section day-list]
     [section plot]]))


