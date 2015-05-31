(ns bed-time.dev
  (:require [figwheel-sidecar.auto-builder :as fig-auto]
            [figwheel-sidecar.core :as fig]
            [weasel.repl.websocket :as weasel]
            [cemerick.piggieback :as piggieback]))

(defn browser-repl []
  (let [repl-env (weasel/repl-env :ip "0.0.0.0" :port 9001)]
    (piggieback/cljs-repl :repl-env repl-env)))

(defn start-figwheel []
  (let [server (fig/start-server { :css-dirs ["resources/public/css"] })
        config {:builds
                [{:source-paths ["env/dev/cljs" "src-cljs"]
                  :compiler {:output-to "resources/public/js/app.js"
                             :output-dir "resources/public/js/out"
                             :source-map "resources/public/js/out.js.map"
                             :source-map-timestamp true
                             :preamble ["react/react.min.js"]}}]
                :figwheel-server server}]
    (fig-auto/autobuild* config)))


