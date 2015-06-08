(ns bed-time.dev
  (:require [figwheel-sidecar.auto-builder :as fig-auto]
            [figwheel-sidecar.core :as fig]
            [clojurescript-build.auto :as auto]
            [bed-time.core :as core]))

(defn run []
  (core/-main))

(def figwheel-state (atom {}))

(defn start-figwheel []
  (let [server (fig/start-server { :css-dirs ["resources/public/css"] })
        config {:builds
                [{:source-paths ["env/dev/cljs" "src-cljs"]
                  :compiler {:output-to "resources/public/js/app.js"
                             :output-dir "resources/public/js/out"
                             :source-map "resources/public/js/out.js.map"
                             :source-map-timestamp true
                             :preamble ["react/react.min.js"]}}]
                :figwheel-server server}
        autobuilder (fig-auto/autobuild* config)]
    (reset! figwheel-state {:server server :autobuilder autobuilder})))

(defn stop-figwheel []
  (fig/stop-server (@figwheel-state :server))
  (auto/stop-autobuild! (@figwheel-state :autobuilder)))


