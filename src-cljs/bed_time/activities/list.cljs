(ns bed-time.activities.list
  (:require [bed-time.activities.form.components :as form]
            [bed-time.subs :refer [subscribe] :as subs]
            [re-frame.core :refer [dispatch]]
            [bed-time.routing :refer [page->href]]
            [bed-time.activities.activities :as activities]
            [bed-time.util :as util])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn end-session-button [session]
  [:input.btn.btn-sm.btn-danger
   {:type     "button"
    :value    "Finish"
    :on-click #(dispatch [:finish-session session])}])

(defn start-session-button [activity]
  [:input.btn.btn-sm.btn-success
   {:type     "button"
    :value    "Start"
    :on-click #(dispatch [:start-session activity])}])

(defn session-action-button [activity]
  (let [current-session (subs/subscribe-current-session)]
    (fn []
      (cond (nil? @current-session)
            (start-session-button activity)
            (= activity (@current-session :activity))
            (end-session-button @current-session)))))

(defn- show-activity [name]
  (let [daily-total (subs/subscribe-aggregates [name :week])
        href (page->href {:handler :activity :route-params {:activity name}})]
    ^{:key name}
    [:tr
     [:td [:a {:href href} name]]
     [:td [session-action-button name]]
     (doall
       (for [day (util/last-weeks-days)]
         ^{:key day}
         [:td (util/time-str (@daily-total day))]))]))

(defn- table-head [last-weeks-days]
  [:thead
   [:tr
    [:td "Activity"]
    [:td]
    (for [day last-weeks-days]
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
  (let [activities (subscribe [:activities])]
    [:tbody
     (doall
       (for [activity-name (keys @activities)]
         ^{:key activity-name}
         [show-activity activity-name]))]))

(defn- table-foot []
  (let [week-totals (subs/subscribe-aggregates [:total :week])]
    [:tfoot
     [totals-row week-totals]
     [unaccounted-row week-totals]]))

(defn- activities-table []
  [:table.table
   [table-head]
   [table-body]
   [table-foot]])

(defn- form-slot []
  (let [current-session (subs/subscribe-current-session)]
    (fn []
      (if (not @current-session)
        [form/form]))))

(defn page []
  [:div
   [:div.page-header
    [:h1 "Activities"]]
   [form-slot]
   [activities-table]])

