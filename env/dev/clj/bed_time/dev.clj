(ns bed-time.dev
  (:require [bed-time.core :as core]
            [figwheel-sidecar.repl-api :as repl]))

(core/-main)

(defn browser-repl []
  (repl/cljs-repl))

