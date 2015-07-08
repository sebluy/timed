(ns bed-time.sessions.form.subs
  (:require [bed-time.framework.db :as db]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.util :as util]))

(defn fields [[key select]]
  (let [text (db/query-db [:page :session-form :fields key :text])
        value (util/str->date text)]
    (condp = select
      :text text
      :message (sessions/string key value)
      :error (sessions/error key value))))

(db/register-derived-query [:page :session-form :fields] fields)

