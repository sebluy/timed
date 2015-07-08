(ns bed-time.pages.pages
  (:require [bed-time.pages.activity :as activity]
            [bed-time.pages.activities :as activities]
            [bed-time.navbar :as navbar]
            [bed-time.framework.db :as db]))

(defonce pages {:activities activities/page
                :activity   activity/page})

(defn- current-page []
  (let [handler (db/subscribe [:page :handler])]
    (fn []
      [(or (pages @handler) :div)])))

(defn view []
  [:div
   [navbar/navbar]
   #_[current-page]])

