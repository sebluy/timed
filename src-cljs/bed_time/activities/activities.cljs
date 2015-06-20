(ns bed-time.activities.activities
  (:require [clojure.string :as string]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.util :as util]))

(defn coerce-activities-to-sorted [new-activities]
  (into {} (map (fn [[activity sessions]]
                  [activity (into (sessions/sessions-map) sessions)])
                new-activities)))

(defn sessions-from-last-n-days [sessions n]
  (take-while #(> (.getTime (first %))
                  (- (.getTime (js/Date.))
                     (* 24 60 60 n 1000)))
              sessions))

(defn weekly-sessions [sessions]
  (sessions-from-last-n-days sessions 7))

(defn daily-sessions [sessions]
  (sessions-from-last-n-days sessions 1))

(defn total-time-spent [sessions]
  (reduce (fn [total session]
            (+ total (sessions/time-spent session)))
          0 sessions))

(defn build-week [sessions]
  (reduce (fn [week [start _ :as session]]
            (let [date (util/midnight start)
                  day (week date)
                  time-spent (sessions/time-spent session)]
              (if day
                (assoc week date (+ day time-spent))
                (assoc week date time-spent))))
          {} (sessions-from-last-n-days sessions 7)))

(defn build-aggregates [activities]
  (reduce (fn [aggregates [name sessions]]
            (let [week (build-week sessions)
                  today (or (week (util/midnight (js/Date.))) 0)
                  weekly (reduce #(+ %1 (second %2)) 0 week)]
              (-> aggregates
                  (update-in [:total :weekly] #(+ weekly %))
                  (update-in [:total :today] #(+ today %))
                  (assoc name {:week   week
                               :weekly weekly
                               :today  today}))))
          {:total {:weekly 0 :today 0}} activities))

(defn add-week-total [aggregates]
  (assoc-in aggregates [:total :week]
            (reduce (fn [week-total [_ activity-aggregates]]
                      (merge-with + (activity-aggregates :week) week-total))
                    {} (dissoc aggregates :total))))

(defn add-hours [date n]
  (js/Date. (+ (.getTime date) (* n 60 60 1000))))

(defn hours-from-now [n]
  (add-hours (js/Date.) n))

(defn error [activity]
  (if (string/blank? activity)
    "Activity cannot be blank"))


