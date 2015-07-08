(ns bed-time.framework.db
  (:require [reagent.core :as reagent])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defonce ^:private db (reagent/atom {}))
(defonce ^:private derived-queries (reagent/atom {}))

(defn register-derived-query [path fn]
  (swap! derived-queries #(assoc-in % path fn)))

(defn query-db [path]
  (get-in @db path))

(defn- query-derived [path]
  (loop [query @derived-queries path path]
    (cond (map? query)
          (recur (query (first path)) (rest path))
          (ifn? query)
          (query path))))

(defn query [path]
  (let [db-value (query-db path)]
    (if (some? db-value)
      db-value
      (query-derived path))))

(defn subscribe [path]
  (reaction (query path)))

(defn transition [transition-fn]
  (swap! db #(transition-fn %)))

