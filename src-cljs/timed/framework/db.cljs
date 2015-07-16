(ns timed.framework.db
  (:require [reagent.core :as reagent]
            [reagent.ratom :as ratom]
            [reagent.impl.batching :as reagent-batching]))

(defonce ^:private db (reagent/atom {}))
(defonce ^:private derived-queries {})
(defonce ^:private active-reactions {})
(defonce ^:private pending-unsubscribe {})

(defn transition [transition-fn]
  (swap! db #(transition-fn %)))

(defn query-db [path]
  (get-in @db path))

(defn- query-derived [path]
  (loop [query derived-queries path path]
    (cond (map? query)
          (recur (query (first path)) (rest path))
          (ifn? query)
          ((query path)))))

(defn query [path]
  (let [db-value (query-db path)]
    (if (some? db-value)
      db-value
      (query-derived path))))

(defn register-derived-query [path fn]
  (set! derived-queries (assoc-in derived-queries path fn)))

(defn batch-unsubscribe []
  (doseq [[path subscription] pending-unsubscribe]
    (.reset-reaction subscription)
    (set! active-reactions (dissoc active-reactions path)))
  (set! pending-unsubscribe {}))

(defn unsubscribe-flush-loop []
  (batch-unsubscribe)
  (reagent-batching/do-after-flush unsubscribe-flush-loop))

(reagent-batching/do-after-flush unsubscribe-flush-loop)

(defn add-active-reaction [path reaction]
  (set! pending-unsubscribe (dissoc pending-unsubscribe path))
  (set! active-reactions (assoc active-reactions path reaction)))

(defn unsubscribe [path subscription]
  (set! pending-unsubscribe (assoc pending-unsubscribe path subscription)))

(defn subscribe [path subscription]
  (if-let [reaction (active-reactions path)]
    reaction
    (let [reaction (ratom/make-reaction
                     #(query path)
                     :on-dispose #(unsubscribe path subscription))]
      (add-active-reaction path reaction)
      reaction)))

(deftype Subscription [^:mutable reaction path]
  Object
  (reset-reaction [_]
    (set! reaction nil))
  IDeref
  (-deref [this]
    (if (nil? reaction)
      (set! reaction (subscribe path this)))
    @reaction))

(defn make-subscription [path]
  (Subscription. nil path))

; this "library" needs some serious refactoring/decoupling from reagent
(defn query-once [path]
  (let [subscription (make-subscription path)
        reaction (ratom/make-reaction (fn [] @subscription))
        value (ratom/capture-derefed (fn [] @reaction) #js{})]
    (ratom/dispose! reaction)
    (batch-unsubscribe)
    value))

