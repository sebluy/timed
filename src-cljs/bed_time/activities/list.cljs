(ns bed-time.activities.list
  (:require [bed-time.activities.form.components :as form]
            [bed-time.sessions.components :as session-components]
            [bed-time.framework.db :as db]
            [bed-time.routing :refer [page->href]]
            [bed-time.util :as util]
            [bed-time.sessions.handlers :as session-handlers]))

(defn- show-activity [name]
  (let [daily-total (db/subscribe [:aggregates name :week])
        href (page->href {:handler :activity :route-params {:activity name}})]
    (fn []
      [:tr
       [:td [:a {:href href} name]]
       [:td [session-components/session-action-button name "btn-sm"]]
       (doall
         (for [day (util/last-weeks-days)]
           ^{:key day}
           [:td (util/time-str (@daily-total day))]))])))

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
  (let [activities (db/subscribe [:activities])]
    (fn []
      [:tbody
       (doall
         (for [activity-name (keys @activities)]
           ^{:key activity-name}
           [show-activity activity-name]))])))

(defn- table-foot []
  (let [week-totals (db/subscribe [:aggregates :total :week])]
    (fn []
      [:tfoot
       [totals-row week-totals]
       [unaccounted-row week-totals]])))

(defn- activities-table []
  [:table.table
   [table-head]
   [table-body]
   [table-foot]])

(defn- form-slot []
  (let [current-session (db/subscribe [:current-session])]
    (fn []
      (if (not @current-session)
        [form/form]))))

(defn page []
  [:div
   [:div.page-header
    [:h1 "Activities"]]
   [form-slot]
   [activities-table]])

