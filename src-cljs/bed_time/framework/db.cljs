(ns bed-time.framework.db
  (:require [reagent.core :as reagent])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defonce ^:private db (reagent/atom {}))

(defonce ^:private virtual-subs (atom {}))

(defn- virtual [path]
  (loop [node @virtual-subs path path]
    (cond (map? node)
          (recur (node (first path)) (rest path))
          (ifn? node)
          (node path))))

(defn register-virtual-sub [path fn]
  (swap! virtual-subs #(assoc-in % path fn)))

(defn query [path]
  (let [db-value (get-in @db path)]
      (cond (not (nil? db-value))
            db-value
            :else
            (virtual path))))

(defn subscribe [path]
  (reaction (query path)))

(defn transition [transition-fn]
  (swap! db #(transition-fn %)))


