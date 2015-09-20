(ns timed.pages.remote
  (:require [sigsub.core :as sigsub :include-macros :true]))

(defn- table-head []
  [:thead
   [:tr
    [:td "Action"]
    [:td "Callback"]]])

(defn- pending-table-body []
  (sigsub/with-reagent-subs
    [pending [:remote :pending]]
    (fn []
      [:tbody
       (doall
         (map-indexed
           (fn [index [action callback]]
             ^{:key index} [:tr [:td (str action)] [:td callback]])
           @pending))])))

(defn- queued-table-body []
  (sigsub/with-reagent-subs
    [queued [:remote :queued]]
    (fn []
      [:tbody
       (doall
         (map-indexed
           (fn [index [action callback]]
             ^{:key index} [:tr [:td (str action)] [:td callback]])
           @queued))])))

(defn pending-table []
  (sigsub/with-reagent-subs
    [pending [:remote :pending]]
    (fn []
      [:div
       [:div.page-header
        [:h1 "Pending Operations"]]
       (if (empty? @pending)
         [:div.jumbotron [:h1.text-center "No pending operations."]]
         [:table.table [table-head] [pending-table-body]])])))

(defn queued-table []
  (sigsub/with-reagent-subs
    [queued [:remote :queued]]
    (fn []
      [:div
       [:div.page-header
        [:h1 "Queued Operations"]]
       (if (empty? @queued)
         [:div.jumbotron [:h1.text-center "No queued operations."]]
         [:table.table [table-head] [queued-table-body]])])))

(defn page []
  [:div.col-md-8.col-md-offset-2
   [pending-table]
   [queued-table]])

