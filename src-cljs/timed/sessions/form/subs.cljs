(ns timed.sessions.form.subs
  (:require [timed.sessions.sessions :as sessions]
            [timed.util :as util]
            [sigsub.core :as sigsub :include-macros :true]))

(defn fields [[key select]]
  (sigsub/with-signals
    [text [:page :session-form :inputs key]]
    (fn []
      (let [value (util/str->date @text)]
        (condp = select
          :text @text
          :message (sessions/string key value)
          :error (sessions/error key value))))))

(sigsub/register-signal-skeleton [:page :session-form :fields] fields)

