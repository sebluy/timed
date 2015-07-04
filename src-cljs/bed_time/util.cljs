(ns bed-time.util
  (:require [clojure.string :as string]))

(defn get-event-value [event]
  (-> event .-target .-value))

(defn str->date [str]
  (if (string/blank? str)
    nil
    (some-> str js/Date.)))

(defn date->str [date]
  (some-> date .toLocaleString))

(defn date-comparator [day1 day2]
  (> (.getTime day1) (.getTime day2)))

(defn datetime-invalid? [datetime]
  (or (nil? datetime) (js/isNaN (.getTime datetime))))

(defonce ms-per-day (* 24 60 60 1000))

(defn days->ms [n]
  (* n ms-per-day))

(defn time-str [millis]
  (if (>= millis (days->ms 1))
    (-> millis
        (/ (days->ms 1))
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

(defn time-diff [start finish]
  (- (.getTime finish)
     (.getTime start)))

(defn midnight [datetime]
  (doto (js/Date. (.getTime datetime)) ; copy because js dates aren't immutable
    (.setHours 0)
    (.setMinutes 0)
    (.setSeconds 0)
    (.setMilliseconds 0)))

(defn day-of-week-str [date]
  (["Sunday" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday"]
    (.getDay date)))

(defn today []
  (midnight (js/Date.)))

(defn n-days-ago [n]
  (js/Date. (- (.getTime (today))
               (days->ms n))))

(defn last-weeks-days []
  (for [n (reverse (range 7))]
    (n-days-ago n)))
