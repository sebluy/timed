(ns bed-time.routing
  (:require [bidi.bidi :as bidi]))

(defonce routes ["activities" {""              :activities
                               ["/" :activity] :activity}])

(defn- route->page [route]
  (bidi/match-route routes route))

(defn- page->route [page]
  (bidi/unmatch-pair routes {:handler (page :handler)
                             :params  (page :route-params)}))

(defn- route->href [route]
  (str "/#" route))

(defn page->href [page]
  (-> page
      page->route
      route->href))



