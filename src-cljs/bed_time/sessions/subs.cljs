(ns bed-time.sessions.subs
  (:require [re-frame.core :refer [register-sub]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn register []
  (register-sub
    :edit-session-form
    (fn [db _]
      (reaction (@db :edit-session-form)))))

