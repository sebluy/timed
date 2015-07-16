(ns timed.remote-handlers
  (:require [cljs.core.async :as async]
            [ajax.core :as ajax]))

; lots of repetition here, begging for refactoring

(defn add-session [session]
  (let [response-chan (async/chan)]
    (ajax/POST
      "/add-session"
      {:params          {:session session}
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    response-chan))

(defn update-session [old-session new-session]
  (let [response-chan (async/chan)]
    (ajax/POST
      "/update-session"
      {:params          {:old-session old-session :new-session new-session}
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    response-chan))

(defn delete-session [session]
  (let [response-chan (async/chan)]
    (ajax/POST
      "/delete-session"
      {:params          {:session session}
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    response-chan))

(defn delete-activity [activity]
   (let [response-chan (async/chan)]
    (ajax/POST
      "/delete-activity"
      {:params          {:activity activity}
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    response-chan))

(defn get-activities []
  (let [response-chan (async/chan)]
    (ajax/GET
      "/activities"
      {:handler         #(async/put! response-chan %)
       :response-format :edn})
    response-chan))

