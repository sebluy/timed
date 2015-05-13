(ns bed-time.routes.home
  (:require [bed-time.layout :as layout]
            [bed-time.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [clj-time.coerce :refer [to-sql-date]]
            [ring.util.response :refer [response]]))

(defn datetime-sql [datetime time-key]
  (let [millis (.getTime datetime)
        date (java.sql.Date. millis)
        time (java.sql.Time. millis)]
    {:date date time-key time}))

(defn home-page []
  (layout/render "home.html"))

(defn hyphen-keys [m]
  (into {} (map #(vector (underscore-to-hyphen (first %)) (second %)) m)))

(defn underscore-to-hyphen [sym]
  (symbol (clojure.string/replace sym #"_" "-")))

(defn get-days []
  (response {:days (map hyphen-keys (db/get-days))}))

(defn wake-up [wake-up-time]
  (db/add-new-day! (datetime-sql wake-up-time :wake_up_time))
  (response {}))

(defn go-to-bed [bed-time]
  (db/add-bed-time! (datetime-sql bed-time :bed_time))
  (response {}))

(defn delete-day [date]
  (db/delete-day! {:date (to-sql-date date)})
  (response {}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/days" [] (get-days))
  (POST "/wake-up" [wake-up-time] (wake-up wake-up-time))
  (POST "/go-to-bed" [bed-time] (go-to-bed bed-time))
  (POST "/delete-day" [date] (delete-day date)))

