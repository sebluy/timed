(ns bed-time.util)

(defn get-event-value [event]
  (-> event .-target .-value))

(defn parse-datetime-str [datetime-str]
  (->> datetime-str (.parse js/Date) js/Date.))

(defn date-comparator [day1 day2]
  (> (.getTime day1) (.getTime day2)))

(defn datetime-invalid? [datetime]
  (or (nil? datetime) (js/isNaN (.getTime datetime))))

(defn hours [millis]
  (/ millis 3600000))

(defn hours-str [millis]
  (.toFixed (hours millis) 2))

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
