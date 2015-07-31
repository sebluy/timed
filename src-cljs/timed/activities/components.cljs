(ns timed.activities.components
  (:require [timed.sessions.components :as session-components]
            [timed.routing :refer [page->href]]
            [timed.util :as util]
            [timed.activities.handlers :as activity-handlers]
            [sigsub.core :as sigsub :include-macros :true]))

(defn delete-button [activity]
  [:input.btn.btn-danger
   {:type     "button"
    :value    "Delete"
    :on-click #(activity-handlers/delete-activity activity)}])

(defn- show-activity [name]
  (let [href (page->href {:handler :activity :route-params {:activity name}})]
    (sigsub/with-reagent-subs
      [daily-total [:aggregates name :week]]
      (fn []
        [:tr
         [:td [:a {:href href} name]]
         [:td [session-components/action-button name "btn-sm" :activity-table]]
         (doall
           (for [day (util/last-weeks-days)]
             ^{:key day}
             [:td (util/time-str (@daily-total day))]))]))))

(defn- table-head []
  [:thead
   [:tr
    [:td "Activity"]
    [:td]
    (for [day (util/last-weeks-days)]
      ^{:key day}
      [:td (util/day-of-week-str day)])]])

(defn- totals-row [week-totals]
  [:tr
   [:td "Total"]
   [:td]
   (doall
     (for [day (util/last-weeks-days)]
       ^{:key day}
       [:td (util/time-str (@week-totals day))]))])

(defn- unaccounted-row [week-totals]
  [:tr
   [:td "Unaccounted"]
   [:td]
   (doall
     (for [day (util/last-weeks-days)]
       ^{:key day}
       [:td (util/time-str (- (util/days->ms 1) (@week-totals day)))]))])

(defn- table-body []
  (sigsub/with-reagent-subs
    [activities [:activities]]
    (fn []
      [:tbody
       (doall
         (for [activity-name (keys @activities)]
           ^{:key activity-name}
           [show-activity activity-name]))])))

(defn- table-foot []
  (sigsub/with-reagent-subs
    [week-totals [:aggregates :total :week]]
    (fn []
      [:tfoot
       [totals-row week-totals]
       [unaccounted-row week-totals]])))

(defn activities-table []
  (sigsub/with-reagent-subs
    [activities [:activities]]
    (fn []
      (cond
        (= @activities :pending)
        [:div.jumbotron [:h1.text-center "Pending"]]
        (seq @activities)
        [:table.table [table-head] [table-body] [table-foot]]
        :else
        [:div.jumbotron [:h1.text-center "No Activities"]]))))

