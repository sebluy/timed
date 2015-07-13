(ns bed-time.sessions.form.transitions
  (:require [bed-time.util :as util]))

(defn open [{:keys [activity start finish new] :as session}]
  (fn [db]
    (assoc-in db [:page :session-form]
              (merge {:activity activity
                      :input    {:start  (util/date->str start)
                                 :finish (util/date->str finish)}}
                     (if new
                       {:new true}
                       {:new false :old-session session})))))

(defn update-field [key text]
  (fn [db]
    (assoc-in db [:page :session-form :input key] text)))

(defn close [db]
  (update-in db [:page] #(dissoc % :session-form)))

