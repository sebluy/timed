(ns bed-time.sessions.handlers
  (:require [cljs.core.async :as async]
            [bed-time.framework.db :as db]
            [ajax.core :as ajax]
            [bed-time.sessions.transitions :as session-transitions]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.handlers :as handlers]
            [bed-time.transitions :as transitions])
  (:require-macros [cljs.core.async.macros :as async]))

(defn update-session-transition-chan [session]
  (let [response-chan (async/chan)
        transition-chan (async/chan)]
    (ajax/POST
      "/update-session"
      {:params          {:session session}
       :handler         #(async/close! response-chan)
       :format          :edn
       :response-format :edn})
    (async/go
      (async/<! response-chan)
      (async/put!
        transition-chan
        (session-transitions/update-session (dissoc session :new))))
    transition-chan))

(defn update-session [session]
  (async/go
    (db/transition (async/<! (update-session-transition-chan session)))))

(defn start-session [activity]
  (db/transition (transitions/add-pending :start-session activity))
  (async/go
    (let [new-session {:activity activity
                       :start (js/Date.)
                       :finish nil
                       :new true}
          transition (async/<! (update-session-transition-chan new-session))]
      (db/transition
        (comp (transitions/remove-pending :start-session) transition)))))

(defn finish-session [session]
  (db/transition (transitions/add-pending :finish-session session))
  (async/go
    (let [finished (assoc session :finish (js/Date.) :new false)
          transition (async/<!(update-session-transition-chan finished))]
      (db/transition
        (comp (transitions/remove-pending :finish-session) transition)))))

(defn delete-session [session]
  (let [response-chan (async/chan)]
    (ajax/POST "/delete-session"
               {:params         {:session session}
                :handler        #(async/close! response-chan)
                :format         :edn
                :reponse-format :edn})
    (async/go
      (async/<! response-chan)
      (db/transition (session-transitions/delete-session session)))))

(defn swap-session-transition-chan [old-session new-session]
  (let [response-chan (async/chan)
        transition-chan (async/chan)]
    (ajax/POST "/swap-session"
               {:params         {:old-session old-session
                                 :new-session new-session}
                :handler        #(async/close! response-chan)
                :format         :edn
                :reponse-format :edn})
    (async/go
      (async/<! response-chan)
      (async/put!
        transition-chan
        (session-transitions/swap-session old-session new-session)))
    transition-chan))

(defn swap-session [old-session new-session]
  (async/go
    (db/transition
      (async/<! (swap-session-transition-chan old-session new-session)))))

