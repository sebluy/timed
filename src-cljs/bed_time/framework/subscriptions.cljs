(ns bed-time.framework.subscriptions
  (:require [bed-time.framework.db :refer [db]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defonce virtual-subs (atom {}))

(defn- virtual [path]
  (loop [node @virtual-subs path path]
    (cond (map? node)
          (recur (node (first path)) (rest path))
          (ifn? node)
          (node path))))

(defn register-virtual-sub [path fn]
  (swap! virtual-subs #(assoc-in % path fn)))

(defn subscribe [path]
  (reaction
    (let [db-value (get-in @db path)]
      (cond (not (nil? db-value))
            db-value
            :else
            (virtual path)))))

