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

(defn session-action-button [activity current-session]
  (let [session @current-session]
    (cond (nil? session)
          (start-session-button activity)
          (= activity (session :activity))
          (end-session-button session))))

(defn- show-activity [name aggregates last-weeks-days current-session]
  (let [href (page->href {:handler :activity :route-params {:activity name}})]
    ^{:key name}
    [:tr
     [:td [:a {:href href} name]]
     [:td [session-action-button name current-session]]
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

(defn- table-body [activities aggregates last-weeks-days current-session]
  [:tbody
   (doall
     (for [activity-name (keys @activities)]
       (show-activity activity-name
                      (@aggregates activity-name)
                      last-weeks-days
                      current-session)))])

(defn- table-foot [aggregates last-weeks-days]
  (let [week-totals (get-in @aggregates [:total :week])]
    [:tfoot
     (totals-row week-totals last-weeks-days)
     (unaccounted-row week-totals last-weeks-days)]))

(defn- activities-table [activities current-session]
  (let [last-weeks-days (util/last-weeks-days)
        aggregates (reaction (println "Refreshing aggregates")
                             (time (activities/add-week-total
                                     (activities/build-aggregates @activities))))]
    (fn []
      [:table.table
       [table-head last-weeks-days]
       [table-body activities aggregates last-weeks-days current-session]
       [table-foot aggregates last-weeks-days]])))

(defn- form-slot [page current-session]
  (let [visible (reaction (nil? @current-session))]
    (fn []
      (if @visible
        [form/form page]))))

(defn page [{:keys [page activities current-session]}]
  [:div
   [:div.page-header
    [:h1 "Activities"]]
   [form-slot page current-session]
   [activities-table activities current-session]])

