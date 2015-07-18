(ns timed.pages.pages
  (:require [timed.pages.activity :as activity]
            [timed.pages.activities :as activities]
            [timed.pages.navbar :as navbar]
            [goog.dom :as dom]
            [timed.util :as util])
  (:require-macros [timed.macros :refer [with-subs]]))

(defonce pages {:activities activities/page
                :activity   activity/page})

(defn- current-page []
  (with-subs [handler [:page :handler]]
    (fn []
      [(or (pages @handler) :div)])))

(defn- swap-text [id text]
  (-> id
      (dom/getElement)
      (dom/setTextContent text)))

; super hack to the rescue
(defn title []
  (with-subs
    [current-session [:current-session]
     time-spent [:current-session-time-spent]]
    (fn []
      (swap-text "title"
                 (if @current-session (util/time-str @time-spent) "Timed"))
      [:div])))

(defn view []
  [:div
   [title] ; because i don't know how to mount a title
   [navbar/navbar]
   [current-page]])

