(ns timed.remote-handlers
  (:require [ajax.core :as ajax]
            [goog.events :as events]
            [timed.db :as db]))

(declare on-response)
(declare on-error)
(declare post-queued)

(def callbacks (atom {}))

(db/transition (fn [db] (assoc db :remote {:pending [] :queued []})))

(events/listen js/window "online"
               (fn []
                 (post-queued)
                 (db/transition (fn [db] (assoc db :status :online)))))
(events/listen js/window "offline"
               (fn [] (db/transition (fn [db] (assoc db :status :offline)))))

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
     :handler         on-response
     :error-handler   on-error
     :format          :edn
     :response-format :edn}))

(defn queue-action [action callback]
  (if (or (seq (db/query [:remote :pending]))
          (seq (db/query [:remote :errors]))
          (= (db/query [:status]) :offline))
    (db/transition
      (fn [db] (update-in db [:remote :queued] conj [action callback])))
    (do (post-actions [action])
        (db/transition
          (fn [db] (assoc-in db [:remote :pending] [[action callback]]))))))

(defn post-queued []
  (let [queued (db/query [:remote :queued])]
    (if (seq queued)
      (do (post-actions (mapv first queued))
          (db/transition
            (fn [db] (update db :remote assoc :pending queued :queued [])))))))

(defn handle-responses [responses]
  (doall (map (fn [[_ callback] response] ((get-callback callback) response))
              (db/query [:remote :pending])
              responses))
  (db/transition (fn [db] (update db :remote assoc :pending []))))

(defn retry-failed []
  (let [errors (db/query [:remote :errors])]
    (post-actions errors)
    (db/transition
      (fn [db]
        (update db :remote
                (fn [remote]
                  (-> remote
                      (assoc :pending errors)
                      (dissoc :errors :error-message))))))))

(defn cancel-failed []
  (db/transition (fn [db] (update db :remote dissoc :errors :error-message)))
  (post-queued))

(defn on-response [responses]
  (handle-responses responses)
  (post-queued))

(defn on-error [error]
  (db/transition (fn [db] (update db :remote assoc
                                  :error-message (str error)
                                  :errors (get-in db [:remote :pending])
                                  :pending []))))

(defn post-action
  ([action] (post-action action :identity))
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

