(ns ^:figwheel-no-load bed-time.app
  (:require [bed-time.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback #(core/mount-components))

(set! reagent.ratom/debug true)
(core/init!)

