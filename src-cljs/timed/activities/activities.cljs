(ns timed.activities.activities
  (:require [timed.sessions.sessions :as sessions]
            [timed.util :as util]
            [clojure.string :as string]))


(defn coerce-activities-to-sorted [new-activities]
  (into {} (map (fn [[name activity]]
                  [name (update-in activity [:sessions]
                                   #(into (sessions/sessions-map) %))])
                  new-activities)))

(defn sessions-from-last-n-days [sessions n]
  (take-while #(> (.getTime (first %))
                  (- (.getTime (js/Date.))
                     (* 24 60 60 n 1000)))
              sessions))

(defn build-week [sessions]
  (reduce (fn [week [start session]]
            (let [date (util/midnight start)
                  day (week date)
                  time-spent (sessions/time-spent session)]
              (if day
                (assoc week date (+ day time-spent))
                (assoc week date time-spent))))
          {} (sessions-from-last-n-days sessions 7)))

(defn build-aggregates [activities]
  (reduce (fn [aggregates [name {sessions :sessions}]]
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

(defn error [activity]
  (if (string/blank? activity)
    "Invalid: Blank"
    (let [invalid-chars (into #{} (re-seq #"\W" activity))]
      (if (seq invalid-chars)
        (str "Invalid: " invalid-chars)))))

