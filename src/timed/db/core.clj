(ns timed.db.core
  (:require
    [yesql.core :refer [defqueries]]))

(def db-spec
  (or (System/getenv "DATABASE_URL")
      {:subprotocol "postgresql"
       :subname "//localhost/bedtime"
       :user "admin"
       :password "admin"}))

(defqueries "sql/queries.sql" {:connection db-spec})

