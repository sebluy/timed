(ns bed-time.sessions.form.deprecacted-handlers)
;  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
;            [bed-time.middleware :refer [remove-v]]
;            [bed-time.util :as util]
;            [bed-time.sessions.sessions :as sessions]))
;
;(register-handler
;  :open-session-form
;  (comp trim-v (path :page))
;  (fn [page [{:keys [activity start finish new] :as session}]]
;    (assoc page :session-form
;              (merge {:activity activity
;                      :fields   {:start  (util/date->str start)
;                                 :finish (util/date->str finish)}}
;                     (if new
;                       {:new true}
;                       {:new false :old-session session})))))
;
;(register-handler
;  :change-session-form-field
;  (comp trim-v (path :page :session-form :fields))
;  (fn [fields [key text]]
;    (assoc fields key text)))
;
;(register-handler
;  :submit-session-form
;  (comp remove-v (path :page))
;  (fn [page]
;    (let [{:keys [activity new old-session fields]} (page :session-form)
;          new-session {:activity activity
;                       :start    (util/str->date (fields :start))
;                       :finish   (util/str->date (fields :finish))
;                       :new      true}]
;      (if (sessions/valid? (sessions/map->vec new-session))
;        (do
;          (if new
;            (dispatch [:update-session new-session])
;            (dispatch [:swap-session new-session old-session]))
;          (dissoc page :session-form))
;        (do (println "Invalid form")
;            page)))))
;
;(register-handler
;  :close-session-form
;  (comp remove-v (path :page))
;  (fn [page]
;    (dissoc page :session-form)))

