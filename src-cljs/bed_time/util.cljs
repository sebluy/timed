(ns bed-time.util)

(defn hours-str [millis precision]
  (.toFixed (hours millis) precision))

(defn hours [millis]
  (/ millis 3600000))
