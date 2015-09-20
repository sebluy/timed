(ns timed.remote-handlers
  (:require [ajax.core :as ajax]
            [timed.db :as db]))

(declare handle-responses)

(def callbacks (atom {}))

; maintain key reference to callback functions so they can be serialized
; to storage when offline
(defn register-callback [key f]
  (swap! callbacks assoc key f))

(defn get-callback [key]
  (or (@callbacks key) identity))

(defn post-actions [actions]
  (ajax/POST
    "/api"
    {:params          actions
     :handler         handle-responses
     :format          :edn
     :response-format :edn}))

(defn queue-action [action callback]
  (if (seq (db/query [:remote :pending]))
    (db/transition
      (fn [db] (update-in db [:remote :queued] conj [action callback])))
    (do (post-actions [action])
        (db/transition
          (fn [db] (assoc-in db [:remote :pending] [[action callback]]))))))

(defn handle-responses [responses]
  (doall (map (fn [[_ callback] response] ((get-callback callback) response))
              (db/query [:remote :pending])
              responses))
  (let [queued (db/query [:remote :queued])]
    (if (seq queued)
      (do (post-actions (mapv first queued))
          (db/transition
            (fn [db] (update db :remote assoc :pending queued :queued []))))
      (db/transition
        (fn [db] (update db :remote assoc :pending [] :queued []))))))

(defn post-action
  ([action] (post-action action identity))
  ([action callback] (queue-action action callback)))

(defn add-session [session]
  (post-action [:add-session session]))

(defn update-session [old-session new-session]
  (post-action [:update-session old-session new-session]))

(defn delete-session [session]
  (post-action [:delete-session session]))

(defn delete-activity [activity]
  (post-action [:delete-activity activity]))

(defn get-activities [callback]
  (post-action [:get-activities] callback))

