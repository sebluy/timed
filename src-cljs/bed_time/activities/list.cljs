(ns bed-time.activities.list
  (:require [bed-time.activities.form.components :as form]
            [re-frame.core :refer [subscribe dispatch]]
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
  (let [current-session (subscribe [:current-session])]
    (fn []
      (cond (nil? @current-session)
            (start-session-button activity)
            (= activity (@current-session :activity))
            (end-session-button @current-session)))))

(defn- show-activity [name aggregates last-weeks-days]
  (let [href (page->href {:handler :activity :route-params {:activity name}})]
    ^{:key name}
    [:tr
     [:td [:a {:href href} name]]
     [:td [session-action-button name]]
     (for [day last-weeks-days]
       ^{:key day}
       [:td (util/time-str (get-in aggregates [:week day]))])]))

(defn- table-head [last-weeks-days]
  [:thead
   [:tr
    [:td "Activity"]
    [:td]
    (for [day last-weeks-days]
      ^{:key day}
      [:td (util/day-of-week-str day)])]])

(defn- totals-row [week-totals last-weeks-days]
  [:tr
   [:td "Total"]
   [:td]
   (doall
     (for [day last-weeks-days]
       ^{:key day}
       [:td (util/time-str (week-totals day))]))])

(defn- unaccounted-row [week-totals last-weeks-days]
  [:tr
   [:td "Unaccounted"]
   [:td]
   (doall
     (for [day last-weeks-days]
       ^{:key day}
       [:td (util/time-str (- (util/days->ms 1) (week-totals day)))]))])

(defn- table-foot [aggregates last-weeks-days]
  (let [week-totals (get-in @aggregates [:total :week])]
    [:tfoot
     (totals-row week-totals last-weeks-days)
     (unaccounted-row week-totals last-weeks-days)]))

(defn- activities-table []
  (let [activities (subscribe [:activities])
        last-weeks-days (util/last-weeks-days)
        aggregates (reaction (activities/add-week-total
                               (activities/build-aggregates @activities)))]
    (fn []
      [:table.table
       [table-head last-weeks-days]
       [:tbody
        (doall
          (for [activity-name (keys @activities)]
            (show-activity
              activity-name (@aggregates activity-name) last-weeks-days)))]
       [table-foot aggregates last-weeks-days]])))

(defn page []
  (let [current-session (subscribe [:current-session])]
    (fn []
      [:div
       [:div.page-header
        [:h1 "Activities"]]
       (if (nil? @current-session)
         [form/form])
       [activities-table]])))

