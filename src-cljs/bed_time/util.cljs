(ns bed-time.util)

(defn hours [millis]
  (/ millis 3600000))

(defn hours-str [millis precision]
  (.toFixed (hours millis) precision))
