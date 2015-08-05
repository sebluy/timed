(ns timed.activities.form.subs
  (:require [timed.activities.activities :as activities]
            [sigsub.core :as sigsub :include-macros :true]))

(defn- error []
  (sigsub/with-signals
    [field [:page :activity-form :field]]
    (fn []
      (activities/error @field))))

(sigsub/register-derived-signal-fn [:page :activity-form :error] error)
