(ns timed.routes.home
  (:require [timed.layout :as layout]
            [timed.db.core :as db]
            [compojure.core :refer [defroutes GET POST ANY]]
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

(defmulti api-action first)

(defmethod api-action :add-session [[_ session]]
  (db/add-session! (db-session session)))

(defmethod api-action :delete-activity [[_ activity]]
  (db/delete-activity! {:activity activity}))

(defmethod api-action :update-session [[_ old-session new-session]]
  (db/delete-session! (db-session old-session))
  (db/add-session! (db-session new-session)))

(defmethod api-action :delete-session [[_ session]]
  (db/delete-session! {:start (sql-datetime (session :start))}))

(defn api [actions]
  (doseq [action actions]
    (api-action action))
  (response nil))

(defroutes home-routes
           (ANY "/api" {actions :body-params} (api actions))
           (GET "/activities" [] (get-activities))
           (GET "/" [] (home-page)))

