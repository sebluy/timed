(ns timed.local-storage
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [timed.db :as db]
            [timed.pages.handlers :as handlers]
            [timed.pages.transitions :as transitions]
            [timed.activities.activities :as activities])
  (:import [goog.storage.mechanism HTML5LocalStorage]
           [goog.storage Storage]))

(defonce storage (Storage. (HTML5LocalStorage.)))

(def default-db {:remote {:pending [] :queued []}})

(def saved #{:remote :activities :tick})

(defn save [value]
  (.set storage :db (pr-str value)))

(defn parse-db [db-string]
  (let [raw-db (reader/read-string db-string)
        unsorted-activites (raw-db :activities)
        sorted-activites (activities/coerce-activities-to-sorted
                           unsorted-activites)]
    (assoc raw-db :activities sorted-activites)))

(defn load []
  (let [db-string (.get storage :db)]
    (if (nil? db-string)
      default-db
      (parse-db db-string))))

(defn save-db []
  (save (select-keys (db/query) saved)))

(defn load-db []
  (let [status (if (.-onLine js/navigator) :online :offline)
        db (merge (load) {:status status})]
    (db/transition (fn [_] db))
    (if (nil? (db :activities)) (handlers/get-activities))
    (if (db :tick) (db/transition transitions/start-tick))))

(events/listenOnce js/window "unload" save-db)


