(ns timed.local-storage
  (:require [cljs.reader :as reader])
  (:import (goog.storage.mechanism HTML5LocalStorage)
           (goog.storage Storage)))

(defonce storage (Storage. (HTML5LocalStorage.)))

(defn save [value]
  (.set storage :db (pr-str value)))

(defn load []
  (reader/read-string (.get storage :db)))


