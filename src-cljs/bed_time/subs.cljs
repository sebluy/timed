(ns bed-time.subs
  (:require [re-frame.db :refer [app-db]]
            [bed-time.sessions.current :as current]
            [bed-time.activities.activities :as activities])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn subscribe [path]
  (reaction (get-in @app-db path)))

(defn subscribe-current-session []
  (reaction (current/extract-current (get-in @app-db [:activities]))))

(defn subscribe-aggregates [path]
  (-> (get-in @app-db [:activities])
      (activities/build-aggregates)
      (activities/add-week-total)
      (get-in path)
      (reaction)))


