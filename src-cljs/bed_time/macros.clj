(ns bed-time.macros
  (:require [cljs.core.async.macros :refer [go-loop]]))

(defmacro go-forever [& body]
  `(go-loop [] ~@body (recur)))

