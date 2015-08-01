(ns timed.pages.pages
  (:require [timed.pages.activity :as activity]
            [timed.pages.activities :as activities]
            [timed.pages.today :as today]
            [timed.pages.navbar :as navbar]
            [goog.dom :as dom]
            [timed.util :as util]
            [sigsub.core :as sigsub :include-macros :true]))

(defonce pages {:activities activities/page
                :activity   activity/page
                :today      today/page})

(defn- current-page []
  (sigsub/with-reagent-subs
    [handler [:page :handler]]
    (fn []
      [(or (pages @handler) :div)])))

(defn- swap-text [id text]
  (-> id
      (dom/getElement)
      (dom/setTextContent text)))

; super hack to the rescue
(defn title []
  (sigsub/with-reagent-subs
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

