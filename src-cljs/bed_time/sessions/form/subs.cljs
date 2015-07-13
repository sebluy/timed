(ns bed-time.sessions.form.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.util :as util])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn fields [[key select]]
  (with-subs
    [text [:page :session-form :input key]]
    (fn []
      (let [value (util/str->date @text)]
        (condp = select
          :text @text
          :message (sessions/string key value)
          :error (sessions/error key value))))))

(db/register-derived-query [:page :session-form :fields] fields)

