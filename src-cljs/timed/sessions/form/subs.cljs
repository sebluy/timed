(ns timed.sessions.form.subs
  (:require [timed.framework.db :as db]
            [timed.sessions.sessions :as sessions]
            [timed.util :as util])
  (:require-macros [timed.macros :refer [with-subs]]))

(defn fields [[key select]]
  (with-subs
    [text [:page :session-form :inputs key]]
    (fn []
      (let [value (util/str->date @text)]
        (condp = select
          :text @text
          :message (sessions/string key value)
          :error (sessions/error key value))))))

(defn pending []
  (with-subs
    [pending-sessions [:pending-sessions]]
    (fn []
      (some (fn [session]
              (= (get-in session [:pending :source]) :session-form))
            @pending-sessions))))

(db/register-derived-query [:page :session-form :fields] fields)
(db/register-derived-query [:page :session-form :pending] pending)

