(ns bed-time.sessions.subs
  (:require [bed-time.framework.db :as db])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn- sessions []
  (with-subs
    [activity [:page :route-params :activity]
     activities [:activities]]
     (fn []
       (get @activities @activity))))

(db/register-derived-query [:page :sessions] sessions)
