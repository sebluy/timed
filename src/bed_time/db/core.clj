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

