(ns bed-time.routes.home
  (:require [bed-time.layout :as layout]
            [bed-time.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [ring.util.response :refer [response]]))

(defn home-page []
  (layout/render "home.html"))

(defn get-bed-times []
  (response {:bed-times (db/get-bed-times)}))

(get-bed-times)

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/bed-times" [] (get-bed-times)))
