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

(defn db-session [{:keys [activity start finish]}]
  {:activity activity
   :start (sql-datetime start)
   :finish (sql-datetime finish)})

(defn get-activities []
  (response
    (reduce (fn [activities session]
              (let [activity-name (session :activity)]
                (update-in activities [activity-name]
                           #(assoc % (session :start) session))))
                {} (db/get-activities))))

(defn delete-activity [activity]
  (db/delete-activity! {:activity activity})
  (response nil))

(defn swap-session [old-session new-session]
  (db/delete-session! (db-session old-session))
  (db/add-session! (db-session new-session))
  (response nil))

(defn update-session [session]
  (Thread/sleep 1000)
  (let [db-sess (db-session session)]
    (if (session :new)
      (db/add-session! db-sess)
      (db/update-session! db-sess))
    (response nil)))

(defn delete-session [{:keys [start]}]
  (db/delete-session! {:start (sql-datetime start)})
  (response nil))

(defroutes home-routes
           (POST "/swap-session" [old-session new-session]
             (swap-session old-session new-session))
           (POST "/update-session" [session] (update-session session))
           (POST "/delete-activity" [activity] (delete-activity activity))
           (GET "/activities" [] (get-activities))
           (GET "/" [] (home-page))
           (POST "/delete-session" [session] (delete-session session)))

