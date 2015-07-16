(ns timed.macros)

(defn- subscribe-bindings [bindings]
  (into []
        (apply concat
               (map (fn [[symbol path]]
                      [symbol `(timed.framework.db/make-subscription ~path)])
                    (partition 2 bindings)))))

(defmacro with-subs [bindings fn]
  (let [subscriptions (subscribe-bindings bindings)]
    `(let ~subscriptions
          ~fn)))

