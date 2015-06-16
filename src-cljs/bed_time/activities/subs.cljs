(ns bed-time.activities.subs
  (:require [re-frame.core :refer [register-sub]]
            [bed-time.activities.form.subs :as form-subs]
            [bed-time.sessions.current :as current])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn register []
  (register-sub
    :activities
    (fn [db _]
      (reaction (@db :activities))))

  (register-sub
    :activity
    (fn [db [_ activity]]
      (reaction (get-in @db [:activities activity]))))

  (register-sub
    :current-session
    (fn [db _]
      (let [activities (reaction (@db :activities))]
        (reaction (current/extract-current @activities)))))

  (form-subs/register))

