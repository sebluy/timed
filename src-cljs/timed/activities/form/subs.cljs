(ns timed.activities.form.subs
  (:require [timed.framework.db :as db]
            [timed.activities.activities :as activities])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn- error []
  (with-subs
    [field [:page :activity-form :field]]
    (fn []
      (activities/error @field))))

(db/register-derived-query [:page :activity-form :error] error)
