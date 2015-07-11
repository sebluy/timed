(ns bed-time.framework.db
  (:require [reagent.core :as reagent]
            [reagent.impl.batching :as reagent-batching])
  (:require-macros [reagent.ratom :refer [reaction]]))

; implement dependent subscriptions

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

; batching unsubscribe

(defonce pending-unsubscribe (atom #{}))

(defn batch-unsubscribe []
  (println "flushing")
  (doseq [path @pending-unsubscribe]
    (println "Removing " path)
    (swap! active-subscriptions #(dissoc % path)))
  (swap! pending-unsubscribe empty))

(defn unsubscribe-flush-loop []
  (batch-unsubscribe)
  (reagent-batching/do-after-flush unsubscribe-flush-loop))

(reagent-batching/do-after-flush unsubscribe-flush-loop)

(defn add-active-subscription [path subscription]
  (println "Adding " path)
;  (swap! pending-unsubscribe #(disj % path))
  (swap! active-subscriptions #(assoc % path {:reaction subscription :count 0})))

(defn unsubscribe [path]
  (let [count (get-in @active-subscriptions [path :count])]
    (if (and count (<= count 1))
      (swap! active-subscriptions #(dissoc % path))
      (swap! active-subscriptions #(update-in % [path :count] dec)))))

(defn increment-count [path]
  (swap! active-subscriptions #(update-in % [path :count] inc)))

(defn subscribe [path]
  (if-let [subscription (get-in @active-subscriptions [path :reaction])]
    (do (increment-count path)
        subscription)
    (let [subscription (reaction (query path))]
      (add-active-subscription path subscription)
      subscription)))

(defn transition [transition-fn]
  (swap! db #(transition-fn %)))

