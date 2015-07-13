(ns bed-time.pages.pages
  (:require [bed-time.pages.activity :as activity]
            [bed-time.pages.activities :as activities]
            [bed-time.navbar :as navbar])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defonce pages {:activities activities/page
                :activity   activity/page})

(defn- current-page []
  (with-subs [handler [:page :handler]]
    (fn []
      [(or (pages @handler) :div)])))

(defn view []
  [:div
   [navbar/navbar]
   #_[current-page]])

