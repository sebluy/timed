(ns bed-time.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clj-time.local :as l]
    [clj-time.coerce :as c]
    [clj-time.core :as t]
    [clj-time.format :as f]))

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost/bedtime"
   :user "admin"
   :password "admin"})

(defqueries "sql/queries.sql" {:connection db-spec})

(defn add-bed-time! [time]
  (insert-bed-time!
    {:time (c/to-sql-time time)})
  time)

(defn add-bed-time-now! []
  (add-bed-time! (c/to-date (l/local-now))))

