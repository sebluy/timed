(ns bed-time.activities.form.subs
  (:require [re-frame.core :refer [register-sub]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn register []
  (register-sub
    :activity-form-error
    (fn [db _]
      (reaction (get-in @db [:activity-form :error]))))

  (register-sub
    :activity-form-field
    (fn [db _]
      (reaction (get-in @db [:activity-form :field])))))


