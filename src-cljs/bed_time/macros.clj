(ns bed-time.macros)

(defn- subscribe-bindings [bindings]
  (into []
        (apply concat
               (map (fn [[symbol path]]
                      [symbol `(bed-time.framework.db/subscribe ~path)])
                    (partition 2 bindings)))))

(defn- unsubscribe-bindings [bindings]
  (map (fn [path]
         `(bed-time.framework.db/unsubscribe ~path))
       (take-nth 2 (rest bindings))))

(defmacro with-subs [bindings fn]
  (let [subscriptions (subscribe-bindings bindings)
        unsubscriptions (unsubscribe-bindings bindings)]
    `(let ~subscriptions
       (reagent.core/create-class
         {:reagent-render ~fn
          :component-will-unmount (fn [] ~@unsubscriptions)}))))

