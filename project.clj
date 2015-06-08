(defproject bed-time "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [selmer "0.8.2"]
                 [com.taoensso/timbre "3.4.0"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.66"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [compojure "1.3.3"]
                 [ring/ring-defaults "0.1.4"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring-middleware-format "0.5.0"]
                 [bouncer "0.3.2"]
                 [prone "0.8.1"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [ring-server "0.4.0"]
                 [ragtime "0.3.8"]
                 [yesql "0.5.0-rc2"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]
                 [org.clojure/clojurescript "0.0-3211" :scope "provided"]
                 [org.clojure/tools.reader "0.9.2"]
                 [reagent "0.5.0"]
                 [cljsjs/react "0.13.1-0"]
                 [reagent-forms "0.5.0"]
                 [reagent-utils "0.1.4"]
                 [secretary "1.2.3"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljs-ajax "0.3.11"]
                 [clj-time "0.9.0"]]

  :min-lein-version "2.0.0"
  :uberjar-name "bed-time.jar"
  :jvm-opts ["-server"]

  :main bed-time.core

  :plugins [[lein-environ "1.0.0"]
            [lein-ancient "0.6.5"]
            [ragtime/ragtime.lein "0.3.8"]
            [lein-cljsbuild "1.0.5"]]
  
  :ragtime
  {:migrations ragtime.sql.files/migrations
   :database
   "jdbc:postgresql://localhost/bedtime?user=admin&password=admin"}
  
  :clean-targets ^{:protect false} ["resources/public/js"]
  
  :cljsbuild
  {:builds
   {:app
    {:source-paths ["src-cljs"]
     :compiler
     {:output-dir "resources/public/js/out"
      :externs ["react/externs/react.js"
                "externs/google_loader_api.js"
                "externs/google_visualization_api.js"]
      :optimizations :none
      :output-to "resources/public/js/app.js"
      :pretty-print true}}}}

  :profiles
  {:uberjar {:omit-source true
             :source-paths ["env/prod/clj"]
             :env {:production true}
             :hooks [leiningen.cljsbuild]
             :cljsbuild
             {:jar true
              :builds
              {:app
               {:source-paths ["env/prod/cljs"]
                :compiler {:optimizations :advanced
                           :pretty-print false}}}} 
             :aot :all}

   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.2"]
                        [pjstadig/humane-test-output "0.7.0"]
                        [figwheel "0.3.3"]
                        [figwheel-sidecar "0.3.3"]]

         :source-paths ["env/dev/clj"]

         :cljsbuild
         {:builds
          {:app
           {:source-paths ["env/dev/cljs"] :compiler {:source-map true}}}} 

         :repl-options {:init-ns bed-time.dev}

         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]

         :env {:dev true}}})
