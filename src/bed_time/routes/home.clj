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

(defn underscore-to-hyphen [sym]
  (symbol (clojure.string/replace sym #"_" "-")))

(defn hyphen-keys [m]
  (into {} (map #(vector (underscore-to-hyphen (first %)) (second %)) m)))

(defn get-days []
  (response
    {:days (into {} (map
                      #(into {} (list (into [] (vals %))))
                      (db/get-days)))}))

(defn wake-up [wake-up-time]
  (db/add-new-day! (datetime-sql wake-up-time :wake_up_time))
  (response {}))

(defn go-to-bed [bed-time]
  (db/add-bed-time! (datetime-sql bed-time :bed_time))
  (response {}))

(add-day {:bed-time (java.util.Date.) :wake-up-time (java.util.Date.)})

(defn add-day [{:keys [bed-time wake-up-time]}]
  (db/add-day! {:bed_time (sql-datetime bed-time)
                :wake_up_time (sql-datetime wake-up-time)})
  (response {}))

(defn delete-day [bed-time]
  (db/delete-day! {:bed_time (sql-datetime bed-time)})
  (response {}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/days" [] (get-days))
  (POST "/add-day" [day] (add-day day))
  (POST "/delete-day" [bed-time] (delete-day bed-time))
  (POST "/wake-up" [wake-up-time] (wake-up wake-up-time))
  (POST "/go-to-bed" [bed-time] (go-to-bed bed-time)))

