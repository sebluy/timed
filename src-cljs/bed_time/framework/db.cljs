(ns bed-time.framework.db
  (:require [reagent.core :as reagent])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defonce ^:private db (reagent/atom {}))
(defonce ^:private derived-queries (reagent/atom {}))
(defonce ^:private active-subscriptions (atom {}))

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

(defn add-active-subscription [path subscription]
  (println "Adding " path)
  (swap! active-subscriptions #(assoc % path subscription)))

(defn unsubscribe [path]
  (println "Removing " path)
  (swap! active-subscriptions #(dissoc % path)))

(for [key (keys @active-subscriptions)]
  (println key))

(defn subscribe [path]
  (if-let [subscription (@active-subscriptions path)]
    subscription
    (let [subscription (reaction (query path))]
      (add-active-subscription path subscription)
      subscription)))

(defn transition [transition-fn]
  (swap! db #(transition-fn %)))

