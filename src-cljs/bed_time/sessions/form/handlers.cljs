(ns bed-time.sessions.form.handlers
  (:require [re-frame.core :refer [register-handler dispatch trim-v path]]
            [bed-time.util :as util]
            [bed-time.sessions.sessions :as sessions]))

(defn remove-v [handler]
  (fn [db _]
    (handler db)))

(defn register []
  (register-handler
    :open-session-form
    trim-v
    (fn [db [{:keys [activity start finish new] :as session}]]
      (assoc db :session-form
                (merge {:activity activity
                        :fields   {:start  (util/date->str start)
                                   :finish (util/date->str finish)}}
                       (if new
                         {:new true}
                         {:new false :old-session session})))))

  (register-handler
    :change-session-form-field
    (comp trim-v (path :session-form :fields))
    (fn [fields [key text]]
      (assoc fields key text)))

  (register-handler
    :submit-session-form
    remove-v
    (fn [db]
      (let [{:keys [activity new old-session fields]} (db :session-form)
            new-session {:activity activity
                         :start    (util/str->date (fields :start))
                         :finish   (util/str->date (fields :finish))
                         :new      true}]
        (if (sessions/valid? (sessions/map->vec new-session))
          (do
            (if new
              (dispatch [:update-session new-session])
              (dispatch [:swap-session new-session old-session]))
            (dissoc db :session-form))
          (do (println "Invalid form")
              db)))))

  (register-handler
    :close-session-form
    remove-v
    (fn [db]
      (dissoc db :session-form))))

