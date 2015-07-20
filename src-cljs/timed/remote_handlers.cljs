(ns timed.remote-handlers
  (:require [cljs.core.async :as async]
            [ajax.core :as ajax]))

(defn post-remote-actions [actions]
  (let [response-chan (async/chan)]
    (ajax/POST
      "/api"
      {:params          actions
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    response-chan))

(defn add-session [session]
  (post-remote-actions [[:add-session session]]))

(defn update-session [old-session new-session]
  (post-remote-actions [[:update-session old-session new-session]]))

(defn delete-session [session]
  (post-remote-actions [[:delete-session session]]))

(defn delete-activity [activity]
  (post-remote-actions [[:delete-activity activity]]))

(defn get-activities []
  (let [response-chan (async/chan)]
    (ajax/GET
      "/activities"
      {:handler         #(async/put! response-chan %)
       :response-format :edn})
    response-chan))

