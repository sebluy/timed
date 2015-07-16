(ns timed.routes.home
  (:require [timed.layout :as layout]
            [timed.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response]])
  (:import (java.sql Timestamp)))

(defn sql-datetime [datetime]
  (some-> datetime
          .getTime
          Timestamp.))

(defn home-page []
  (layout/render "home.html"))

(defn db-session [{:keys [activity start finish]}]
  {:activity activity
   :start (sql-datetime start)
   :finish (sql-datetime finish)})

(defn get-activities []
  (response
    (reduce (fn [activities session]
              (let [activity-name (session :activity)]
                (update-in activities [activity-name]
                           #(assoc-in % [:sessions (session :start)] session))))
                {} (db/get-activities))))

(defn delete-activity [activity]
  (db/delete-activity! {:activity activity})
  (response nil))

(defn update-session [old-session new-session]
  (db/delete-session! (db-session old-session))
  (db/add-session! (db-session new-session))
  (response nil))

(defn add-session [session]
  (db/add-session! (db-session session))
  (response nil))

(defn delete-session [{:keys [start]}]
  (db/delete-session! {:start (sql-datetime start)})
  (response nil))

(defroutes home-routes
           (POST "/add-session" [session] (add-session session))
           (POST "/update-session" [old-session new-session]
             (update-session old-session new-session))
           (POST "/delete-session" [session] (delete-session session))
           (POST "/delete-activity" [activity] (delete-activity activity))
           (GET "/activities" [] (get-activities))
           (GET "/" [] (home-page)))

