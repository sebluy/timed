(ns timed.sessions.handlers
  (:require [cljs.core.async :as async]
            [timed.framework.db :as db]
            [timed.sessions.transitions :as session-transitions]
            [timed.remote-handlers :as remote-handlers])
  (:require-macros [cljs.core.async.macros :as async]))

(defn add-session
  ([session source] (add-session session source identity))
  ([session source post-transition]
   (db/transition (session-transitions/add-session
                    (assoc session :pending {:action :add :source source})))
   (async/go
     (async/<! (remote-handlers/add-session session))
     (db/transition
       (comp
         post-transition
         (session-transitions/update-session session #(dissoc % :pending)))))))

(defn update-session
  ([old-session new-session source]
   (update-session old-session new-session source identity))
  ([old-session new-session source post-transition]
   (db/transition (session-transitions/update-session
                    old-session
                    #(assoc % :pending {:action :update :source source})))
   (async/go
     (async/<! (remote-handlers/update-session old-session new-session))
     (db/transition
       (comp
         post-transition
         (session-transitions/update-session
           old-session
           #(identity new-session)))))))

(defn start-session
  ([activity source] (start-session activity source identity))
  ([activity source post-transition]
   (let [session {:activity activity
                  :start    (js/Date.)
                  :finish   nil}]
     (db/transition
       (session-transitions/add-session
         (assoc session :pending {:source source :action :start})))
     (async/go
       (async/<! (remote-handlers/add-session session))
       (db/transition
         (comp
           post-transition
           (session-transitions/update-session
             session
             #(dissoc % :pending))))))))

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
