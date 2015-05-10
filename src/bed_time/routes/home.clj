(ns bed-time.routes.home
  (:require [bed-time.layout :as layout]
            [bed-time.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [clj-time.coerce :refer [to-sql-date]]
            [ring.util.response :refer [response]]))

(defn home-page []
  (layout/render "home.html"))

(defn get-days []
  (response {:days (db/get-days)}))

(defn wake-up []
  (response {:wake-up-time (db/add-today!)}))

(defn go-to-bed []
  (response {:bed-time (db/add-bed-time-now!)}))

(defn delete-day [date]
  (db/delete-day! {:date (to-sql-date date)})
  (response {}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/days" [] (get-days))
  (POST "/wake-up" [] (wake-up))
  (POST "/go-to-bed" [] (go-to-bed))
  (POST "/delete-day" [date] (delete-day date)))
