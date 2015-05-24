(ns bed-time.bed-time
  (:require [bed-time.days :refer [days get-days]]
            [bed-time.day-list :refer [day-list]]
            [bed-time.form :refer [update-form]]
            [bed-time.header :refer [header]]))

(defn page-header [element]
  [:div.page-header
   [element]])

(defn bed-time-page []
  (get-days)
  (fn []
    [:div.col-md-6.col-md-offset-3
     [page-header header]
     [page-header update-form]
     [day-list]]))

