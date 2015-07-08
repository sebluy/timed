(ns bed-time.sessions.handlers
  (:require [cljs.core.async :as async]
            [bed-time.framework.db :as db]
            [ajax.core :as ajax]
            [bed-time.sessions.transitions :as session-transitions]
            [bed-time.sessions.sessions :as sessions]
            [bed-time.handlers :as handlers]
            [bed-time.remote-handlers :as remote-handlers]
            [bed-time.transitions :as transitions])
  (:require-macros [cljs.core.async.macros :as async]))

#_(defn update-session-transition-chan [session]
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

#_(defn update-session [session]
  (async/go
    (db/transition (async/<! (update-session-transition-chan session)))))

(defn start-session
  ([activity source] (start-session activity source identity))
  ([activity source pre-transition]
   (let [session {:activity activity
                  :start    (js/Date.)
                  :finish   nil}]
     (db/transition
       (comp pre-transition
             (session-transitions/add-session
               (assoc session :pending {:source source :action :start}))))
     (async/go
       (async/<! (remote-handlers/add-session session))
       (db/transition
         (session-transitions/update-session session #(dissoc % :pending)))))))

(defn finish-session [session source]
  (db/transition
    (session-transitions/update-session
      session
      #(assoc % :pending {:source source :action :finish})))
  (async/go
    (let [finished (assoc session :finish (js/Date.))]
      (async/<! (remote-handlers/update-session session finished))
      (db/transition
        (session-transitions/update-session session #(identity finished))))))

(defn delete-session [session]
  (db/transition
    (session-transitions/update-session
      session
      #(assoc % :pending {:action :delete})))
  (async/go
    (async/<! (remote-handlers/delete-session session))
    (db/transition
      (session-transitions/delete-session session))))

#_(defn swap-session-transition-chan [old-session new-session]
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

#_(defn swap-session [old-session new-session]
  (async/go
    (db/transition
      (async/<! (swap-session-transition-chan old-session new-session)))))

