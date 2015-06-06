(ns bed-time.util)

(defn hours [millis]
  (/ millis 3600000))

(defn hours-str [millis precision]
  (.toFixed (hours millis) precision))

(defn time-of-day [datetime]
  [(.getHours datetime)
   (.getMinutes datetime)
   (.getSeconds datetime)
   (.getMilliseconds datetime)])

(defn midnight [datetime]
  (doto (js/Date. (.getTime datetime)) ; copy because js dates arent immutable
    (.setHours 0)
    (.setMinutes 0)
    (.setSeconds 0)
    (.setMilliseconds 0)))
