(ns bed-time.util)

(defn get-event-value [event]
  (-> event .-target .-value))

(defn str->date [str]
  (some->> str js/Date.))

(defn date->str [date]
  (some-> date .toLocaleString))

(defn date-comparator [day1 day2]
  (> (.getTime day1) (.getTime day2)))

(defn datetime-invalid? [datetime]
  (or (nil? datetime) (js/isNaN (.getTime datetime))))

(defn days [n]
  (* n 24 60 60 1000))

(defn time-str [millis]
  (if (>= millis (days 1))
    (-> millis
        (/ (days 1))
        (.toFixed 2)
        (str " days"))
    (-> millis
        (js/Date.)
        (.toUTCString)
        (subs 17 25))))

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
