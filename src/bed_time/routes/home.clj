(ns bed-time.routes.home
  (:require [bed-time.layout :as layout]
            [bed-time.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [ring.util.response :refer [response]]))

(defn sql-datetime [datetime]
  (some-> datetime
          .getTime
          java.sql.Timestamp.))

(defn home-page []
  (layout/render "home.html"))

; form => {activity {start finish}}
(defn get-activities []
  (response
    (reduce (fn [activities session]
              (let [activity-name (session :activity)]
                (update-in activities [activity-name]
                           #(merge % {(session :start) (session :finish)}))))
                {} (db/get-activities))))

(defn delete-activity [activity]
  (db/delete-activity! {:activity activity})
  (response nil))

(defn update-session [{:keys [activity start finish new]}]
  (let [db-session {:activity activity
                    :start    (sql-datetime start)
                    :finish   (sql-datetime finish)}]
    (if new
      (db/add-session! db-session)
      (db/update-session! db-session))
    (response nil)))

(defn get-days []
  (response
    {:days (into {} (map #(into {} (list [(% :start) (% :finish)]))
                         (db/get-sessions {:activity "Sleeping"})))}))

(defn update-day [{:keys [bed-time wake-up-time new]}]
  (let [db-day {:start  (sql-datetime bed-time)
                :finish (sql-datetime wake-up-time)}]
    (if new
      (db/add-session! (merge {:activity "Sleeping"} db-day))
      (db/update-session! db-day))
    (response nil)))

(defn delete-session [[start _]]
  (db/delete-session! {:start (sql-datetime start)})
  (response nil))

(defroutes home-routes
           (POST "/update-session" [session] (update-session session))
           (POST "/delete-activity" [activity] (delete-activity activity))
           (GET "/activities" [] (get-activities))
           (GET "/" [] (home-page))
           (GET "/days" [] (get-days))
           (POST "/update-day" [day] (update-day day))
           (POST "/delete-session" [session] (delete-session session)))

