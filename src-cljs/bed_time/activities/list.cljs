(ns bed-time.activities.list
  (:require [bed-time.activities.form.components :as form]
            [re-frame.core :refer [subscribe dispatch]]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.activities.activities :as activities]
            [bed-time.util :as util])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn- delete-button [activity]
  [:input.btn.btn-sm.btn-danger.pull-right
   {:type     "button"
    :value    "Delete!"
    :on-click #(dispatch [:post-delete-activity activity])}])

(defn session-action-button [activity]
  (let [current-session (subscribe [:current-session])]
    (fn []
      (cond (nil? @current-session)
            (sessions/start-session-button activity)
            (= activity (@current-session :activity))
            (sessions/end-session-button @current-session)))))

(defn week []
  (let [today (util/midnight (js/Date.))
        days-ago #(js/Date. (- (.getTime today) (util/days %)))]
    (for [n (reverse (range 7))]
      (days-ago n))))

(defn- show-day [name aggregates]
  ^{:key name}
  [:tr
   [:td [:a {:href (str "/#activities/" name)} name]]
   (for [day (week)]
     [:td (util/time-str (get-in aggregates [:week day]))])
;   [:td (util/time-str (aggregates :weekly))]
;   [:td (util/time-str (aggregates :today))]
   [:td [session-action-button name]]
   [:td (delete-button name)]])

(defn day-of-week-str [day-pos]
  (["Sunday" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday"]
    day-pos))

(defn- activities-list []
  (let [activities (subscribe [:activities])
        aggregates (reaction (activities/build-aggregates @activities))]
    (fn []
      (println @aggregates)
      [:table.table
       [:thead
        [:tr
         [:td "Activity"]
         (for [day (week)]
           [:td (day-of-week-str (.getDay day))])
;         [:td "Weekly Time Spent"]
;         [:td "Time Spent Today"]
         [:td] [:td]]]
       [:tbody
        (doall
          (for [activity-name (keys @activities)]
            (show-day activity-name (@aggregates activity-name))))]
       [:tfoot
        [:tr
         [:td "Unaccounted"]
         [:td (util/time-str (- (* 7 24 60 60 1000)
                                (get-in @aggregates [:total :weekly])))]
         [:td (util/time-str (- (* 24 60 60 1000)
                                (get-in @aggregates [:total :today])))]
         [:td] [:td]]]])))

(defn page []
  (let [current-session (subscribe [:current-session])]
    (fn []
      [:div
       [:div.page-header
        [:h1 "Activities"]]
       (if (nil? @current-session)
         [form/form])
       [activities-list]])))

