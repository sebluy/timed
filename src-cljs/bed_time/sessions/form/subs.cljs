(ns bed-time.sessions.form.subs
  (:require [re-frame.core :refer [register-sub]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn register []
  (register-sub
    :session-form
    (fn [db _]
      (reaction (@db :session-form)))))

