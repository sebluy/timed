(ns bed-time.machine
  (:require [cljs.core.async
             :refer [<! >! put! sub pub chan timeout alts! filter< take! close!]]
            [bed-time.framework.events :as events]
            [bed-time.framework.db :as db]
            [bed-time.routing :as routing])
  (:require-macros [bed-time.macros :refer [go-forever]]
                   [cljs.core.async.macros :refer [go go-loop alt!]]))


#_(defmulti handle first)

#_(defmethod handle :navigate-route [[_ route]]
  (db/update-state :set-page (routing/route->page route)))

#_(defmethod handle :start-tick [[_]]
    (let [interval (js/setInterval #(dispatch [:tick]) 1000)]
      (assoc db :tick {:now (js/Date.) :interval interval})))

(defn debug [x]
  (println x)
  x)

(defn run-navigation []
  (let [nav-chan (chan)]
    (sub events/events-pub :navigate-route nav-chan)
    (go-forever
      (let [route ((<! nav-chan) :route)]
        (db/update-state :set-page (routing/route->page route))))))


#_(defn run-tick []
  (let [tick-chan (chan)]
    (sub events/events-pub :update-tick tick-chan)
    (let [tick-pub (pub tick-chan :command)
          start-chan (chan)
          stop-chan (chan)
          sync-chan (chan)]
      (sub tick-pub :start start-chan)
      (sub tick-pub :stop stop-chan)
      (sub tick-pub :sync sync-chan)
      (go-loop
        []
        (while
          (let [[{command :command} _] (alts! [start-chan sync-chan stop-chan])]
            (not= :start command)))
        (alt!
          sync-chan (println "Tick")
          stop-chan (println "Stopping")
          (timeout 1000) (println "Tick"))
        (recur)))))


; create a fn that creates a new channel that takes but ignores certain values
; create a fn that takes while it hasnt yet found a certain value
; aka dont use any "advanced core.async fns"

#_(defn async-some [predicate input-chan]
  (go (loop []
        (let [msg (debug (<! input-chan))]
          (println "got from " predicate)
          (if (predicate msg)
            msg
            (recur))))))

(defn start-tick []
  (let [stop-chan (chan)]
    (go
      (loop []
        (alt!
          (timeout 1000) (do (println "tick") (recur))
          stop-chan nil)))
    stop-chan))

(defn stop-tick [stop-chan]
  (put! stop-chan :stop))

(defn run-tick []
  (let [tick-chan (chan)]
    (sub events/events-pub :update-tick tick-chan)
    (go-forever
      (while (not= :start ((<! tick-chan) :command)))
      (loop [stop-chan (start-tick)]
        (condp = ((<! tick-chan) :command)
          :sync (do (stop-tick stop-chan)
                    (recur (start-tick)))
          :stop (stop-tick stop-chan)
          (recur stop-chan))))))


#_(let [c (chan)]
  (put! c 1)
  (put! c 2)
  (go (println (<! c))
      (println (<! c))))

#_(pub tick-chan :command)
#_(go-forever
  (let [command ((<! tick-chan) :command)]
    (cond (= command :start)
          (go-loop []
                   (alt!
                     (timeout 1000) (do (println "Tick")
                                        (recur))
                     stop-chan (println "Stopping"))))))

(defn run []
  (run-navigation)
  (run-tick))
;  (go-forever
;    (let [event-v (<! events/events-chan)]

#_(register-handler
  :tick
  (fn [db _]
    (assoc-in db [:tick :now] (js/Date.))))

#_(register-handler
  :sync-tick
  (fn [db _]
    (js/clearInterval (get-in db [:tick :interval]))
    (dispatch [:start-tick])
    db))


