(ns bed-time.core
  (:require [bed-time.handler :refer [app init destroy]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :as reload]
            [figwheel-sidecar.auto-builder :as fig-auto]
            [figwheel-sidecar.core :as fig]
            [environ.core :refer [env]])
  (:gen-class))

(defonce server (atom nil))

(defn parse-port [port]
  (Integer/parseInt (or port (env :port) "3000")))

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

(defn start-server [port]
  (init)
  (reset! server
          (run-jetty
            (if (env :dev) (reload/wrap-reload #'app) app)
            {:port port
             :join? false})))

(defn stop-server []
  (when @server
    (destroy)
    (.stop @server)
    (reset! server nil)))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (if (env :dev) (start-figwheel))
    (start-server port)))

