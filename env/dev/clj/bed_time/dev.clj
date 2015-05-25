(ns bed-time.dev
  (:require [figwheel-sidecar.auto-builder :as fig-auto]
            [figwheel-sidecar.core :as fig]))

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

