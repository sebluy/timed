(ns bed-time.handlers
  (:require [bed-time.activities.handlers]
            [bed-time.activities.form.handlers]
            [bed-time.sessions.handlers]
            [bed-time.sessions.form.handlers]
            [re-frame.core :refer [register-handler trim-v debug]]))

(register-handler
  :set-page
  trim-v
  (fn [db [page]]
    (merge db {:page page})))

