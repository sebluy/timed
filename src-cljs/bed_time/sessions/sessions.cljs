(ns bed-time.sessions.sessions
  (:require [bed-time.util :as util]
            [re-frame.core :refer [dispatch]]))

(defn sessions-map [] (sorted-map-by util/date-comparator))

(defn valid? [[_ finish]]
  (not (nil? finish)))

(defn time-spent [[start finish :as session]]
  (if (valid? session)
    (- (.getTime finish)
       (.getTime start))
    0))

(defn end-session-button
  ([session] (end-session-button session nil))
  ([session class]
   [:input.btn.btn-sm.btn-danger
    {:type     "button"
     :class    class
     :value    (str "End " (session :activity) " Session")
     :on-click #(dispatch [:end-session session])}]))

(defn start-session-button [activity]
  [:input.btn.btn-sm.btn-success
   {:type     "button"
    :value    "Start Session"
    :on-click #(dispatch [:new-session activity])}])
