(ns ^:figwheel-no-load bed-time.app
  (:require [bed-time.core :as core]
            [figwheel.client :as figwheel :include-macros true]
            [clojure.browser.repl :as repl]
            [reagent.core :as r]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback core/init!)

(core/init!)
