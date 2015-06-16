(ns bed-time.sessions.current)

;; Look at using async channels to watch activity change events
;; and update current accordingly

#_(defn update-current-session [{:keys [activity start finish] :as session}]
  (cond (and finish (nil? (get-in @state/activities [activity start])))
        (reset! state/current-session nil)
        (nil? finish)
        (reset! state/current-session session)))

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

