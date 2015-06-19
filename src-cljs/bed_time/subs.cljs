(ns bed-time.subs
  (:require [bed-time.activities.subs :as activity-subs]
            [bed-time.sessions.subs :as session-subs]
            [re-frame.core :refer [register-sub]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn register []
  (activity-subs/register)
  (session-subs/register)

  (register-sub
    :page
    (fn [db _]
      (reaction (@db :page)))))

