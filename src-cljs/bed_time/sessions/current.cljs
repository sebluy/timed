(ns bed-time.sessions.current)

(defn unfinished-activities [activities]
  (reduce (fn [unfinished [activity-name sessions]]
            (let [new-unfinished
                  (map (fn [[start finish]]
                         {:activity activity-name :start start :finish finish})
                       (filter (fn [[_ finish]] (nil? finish))
                               sessions))]
              (into unfinished new-unfinished)))
          () activities))

(defn extract-current [activities]
  (let [unfinished (unfinished-activities activities)]
    (assert (not (> (count unfinished) 1)))
    (first unfinished)))

