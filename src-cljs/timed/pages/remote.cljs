(ns timed.pages.remote
  (:require [sigsub.core :as sigsub :include-macros :true]
            [timed.pages.handlers :as handlers]))

(defn- table-head []
  [:thead
   [:tr
    [:td "Action"]
    [:td "Callback"]]])

(defn- error-table-body []
  (sigsub/with-reagent-subs
    [errors [:remote :errors]]
    (fn []
      [:tbody
       (doall
         (map-indexed
           (fn [index [action callback]]
             ^{:key index} [:tr [:td (str action)] [:td (str callback)]])
           @errors))])))

(defn- pending-table-body []
  (sigsub/with-reagent-subs
    [pending [:remote :pending]]
    (fn []
      [:tbody
       (doall
         (map-indexed
           (fn [index [action callback]]
             ^{:key index} [:tr [:td (str action)] [:td (str callback)]])
           @pending))])))

(defn- queued-table-body []
  (sigsub/with-reagent-subs
    [queued [:remote :queued]]
    (fn []
      [:tbody
       (doall
         (map-indexed
           (fn [index [action callback]]
             ^{:key index} [:tr [:td (str action)] [:td (str callback)]])
           @queued))])))

(defn retry-button []
  [:input.btn.btn-success
   {:type     "button"
    :value    "Retry"
    :on-click handlers/retry-failed-remote}])

(defn cancel-button []
  [:input.btn.btn-danger
   {:type     "button"
    :value    "Cancel"
    :on-click handlers/cancel-failed-remote}])

(defn error-table []
  (sigsub/with-reagent-subs
    [errors [:remote :errors]
     error-message [:remote :error-message]]
    (fn []
      [:div
       [:div.page-header
        [:h1 "Failed Operations"]]
       (if (empty? @errors)
         [:div.jumbotron [:h1.text-center "No failed operations."]]
         [:div
          [:h4 @error-message
           [:p.pull-right.btn-toolbar
            [retry-button]
            [cancel-button]]]
          [:table.table [table-head] [error-table-body]]])])))

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
   [error-table]
   [pending-table]
   [queued-table]])

