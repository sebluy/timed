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

(defn get-days []
  (response
    {:days (into {} (map #(into {} (list [(% :start) (% :finish)]))
                         (db/get-sessions {:activity "Sleeping"})))}))

(defn update-day [{:keys [bed-time wake-up-time new]}]
  (let [db-day {:start (sql-datetime bed-time)
                :finish (sql-datetime wake-up-time)}]
    (if new
      (db/add-session! (merge {:activity "Sleeping"} db-day))
      (db/update-session! db-day))
    (response {})))

(defn delete-day [[bed-time _]]
  (db/delete-session! {:start (sql-datetime bed-time)})
  (response {}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/days" [] (get-days))
  (POST "/update-day" [day] (update-day day))
  (POST "/delete-day" [day] (delete-day day)))

