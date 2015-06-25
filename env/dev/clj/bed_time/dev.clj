(ns bed-time.dev
  (:require [bed-time.core :as core]
            [figwheel-sidecar.repl-api :as repl]))

(defn browser-repl []
  (repl/cljs-repl))

(core/-main)
(browser-repl)


