(ns timed.pages.pages
  (:require [timed.pages.activity :as activity]
            [timed.pages.activities :as activities]
            [timed.pages.navbar :as navbar])
  (:require-macros [timed.macros :refer [with-subs]]))

(defonce pages {:activities activities/page
                :activity   activity/page})

(defn- current-page []
  (with-subs [handler [:page :handler]]
    (fn []
      [(or (pages @handler) :div)])))

(defn view []
  [:div
   [navbar/navbar]
   [current-page]])

