(ns timed.remote-handlers
  (:require [cljs.core.async :as async]
            [ajax.core :as ajax]
            [timed.framework.db :as db]
            [timed.pages.transitions :as transitions]))

(defn post-actions [actions]
  (let [response-chan (async/chan)]
    (ajax/POST
      "/api"
      {:params          actions
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    response-chan))

(db/query-once [:offline-actions])

(defn queue-action [action]
  (if (= :online (db/query-once [:mode]))
    (post-actions [action])
    (db/transition (transitions/add-offline-action action))))

(defn add-session [session]
  (queue-action [:add-session session]))

(defn update-session [old-session new-session]
  (queue-action [:update-session old-session new-session]))

(defn delete-session [session]
  (queue-action [:delete-session session]))

(defn delete-activity [activity]
  (queue-action [:delete-activity activity]))

(defn get-activities []
  (let [response-chan (async/chan)]
    (ajax/GET
      "/activities"
      {:handler         #(async/put! response-chan %)
       :response-format :edn})
    response-chan))

