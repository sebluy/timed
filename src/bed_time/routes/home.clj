(ns bed-time.routes.home
  (:require [bed-time.layout :as layout]
            [bed-time.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [ring.util.response :refer [response]]))

(defn sql-datetime [datetime]
  (java.sql.Timestamp. (.getTime datetime)))

(defn home-page []
  (layout/render "home.html"))

(defn get-days []
  (response
    {:days (into {} (map #(into {} (list [(% :bed_time) (% :wake_up_time)]))
                         (db/get-days)))}))

(defn update-day [{:keys [bed-time wake-up-time]}]
  (db/update-day! {:bed_time (sql-datetime bed-time)
                   :wake_up_time (sql-datetime wake-up-time)})
  (response {}))

(defn add-day [{:keys [bed-time wake-up-time]}]
  (db/add-day! {:bed_time (sql-datetime bed-time)
                :wake_up_time (sql-datetime wake-up-time)})
  (response {}))

(defn delete-day [[bed-time _]]
  (println bed-time)
  (db/delete-day! {:bed_time (sql-datetime bed-time)})
  (response {}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/days" [] (get-days))
  (POST "/update-day" [day] (update-day day))
  (POST "/add-day" [day] (add-day day))
  (POST "/delete-day" [day] (delete-day day)))

