(ns bed-time.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clj-time.coerce :as c]
    [clj-time.core :as t]))

(def db-spec
  (or (System/getenv "DATABASE_URL")
      {:subprotocol "postgresql"
       :subname "//localhost/bedtime"
       :user "admin"
       :password "admin"}))

(defqueries "sql/queries.sql" {:connection db-spec})

(defn add-bed-time-now! []
  (let [now (t/now)]
    (add-bed-time!
      {:date (c/to-sql-date now)
       :bed_time (c/to-sql-time now)})
    now))

(defn add-today! []
  (let [now (t/now)]
    (add-new-day!
      {:date (c/to-sql-date now)
       :wake_up_time (c/to-sql-time now)})
    now))

