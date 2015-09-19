(ns timed.local-storage
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [timed.db :as db]
            [timed.pages.handlers :as handlers]
            [timed.pages.transitions :as transitions]
            [timed.activities.activities :as activities])
  (:import (goog.storage.mechanism HTML5LocalStorage)
           (goog.storage Storage)))

(defonce storage (Storage. (HTML5LocalStorage.)))

(def saved #{:mode :offline-actions :activities :tick})

(defn save [value]
  (.set storage :db (pr-str value)))

(defn load []
  (let [raw-db (reader/read-string (.get storage :db))
        unsorted-activites (raw-db :activities)
        sorted-activites (activities/coerce-activities-to-sorted
                           unsorted-activites)]
    (assoc raw-db :activities sorted-activites)))

(defn save-db []
  (save (select-keys (db/query) saved)))

(defn load-db []
  (let [db (load)]
    (db/transition (fn [_] db))
    (if (nil? (db :activities)) (handlers/get-activities))
    (if (nil? (db :mode)) (handlers/go-online))
    (if (db :tick) (db/transition transitions/start-tick))))

(events/listenOnce js/window, "unload", save-db)
