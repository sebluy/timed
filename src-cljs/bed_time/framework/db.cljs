(ns bed-time.framework.db
  (:require [reagent.core :as reagent]
            [reagent.ratom :as ratom]
            [reagent.impl.batching :as reagent-batching]))

(defonce ^:private db (reagent/atom {}))
(defonce ^:private derived-queries {})
(defonce ^:private active-reactions {})
(defonce ^:private pending-unsubscribe #{})

(defn transition [transition-fn]
  (swap! db #(transition-fn %)))

(defn query-db [path]
  (get-in @db path))

(defn- query-derived [path]
  (let [query (derived-queries path)]
    ((query) path)))

(defn query [path]
  (let [db-value (query-db path)]
    (if (some? db-value)
      db-value
      (query-derived path))))

(defn register-derived-query [path fn]
  (set! derived-queries (assoc derived-queries path fn)))

(defn batch-unsubscribe []
  (println "flushing")
  (doseq [path pending-unsubscribe]
    (println "Removing " path)
    (set! active-reactions (dissoc active-reactions path)))
  (set! pending-unsubscribe #{}))

(defn unsubscribe-flush-loop []
  (batch-unsubscribe)
  (reagent-batching/do-after-flush unsubscribe-flush-loop))

(reagent-batching/do-after-flush unsubscribe-flush-loop)

(defn add-active-reaction [path reaction]
  (println "adding " path)
  (set! pending-unsubscribe (disj pending-unsubscribe path))
  (set! active-reactions (assoc active-reactions path reaction)))

(defn unsubscribe [path]
  (set! pending-unsubscribe (conj pending-unsubscribe path)))

(defn subscribe [path]
  (if-let [reaction (active-reactions path)]
    reaction
    (let [reaction (ratom/make-reaction
                     #(query path)
                     :on-dispose #(do (println "Disposing" path)
                                      (unsubscribe path)))]
      (add-active-reaction path reaction)
      reaction)))

(deftype Subscription [^:mutable reaction path]
  IDeref
  (-deref [_]
    (if (nil? reaction)
      (set! reaction (subscribe path)))
    @reaction))

(defn make-subscription [path]
  (Subscription. nil path))


