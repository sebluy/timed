(ns bed-time.sessions.current
  (:require [bed-time.state :as state]
            [ajax.core :as ajax]))

(defn update-current-session [{:keys [activity start finish] :as session}]
  (cond (and finish (nil? (get-in @state/activities [activity start])))
        (reset! state/current-session nil)
        (nil? finish)
        (reset! state/current-session session)))

(defn unfinished-activities []
  (reduce (fn [unfinished [activity-name sessions]]
            (let [new-unfinished
                  (map (fn [[start finish]]
                         {:activity activity-name :start start :finish finish})
                       (filter (fn [[_ finish]] (nil? finish))
                               sessions))]
              (into unfinished new-unfinished)))
          () @state/activities))

(defn extract-current []
  (let [unfinished (unfinished-activities)]
    (if (> (count unfinished) 1)
      (println "More than one unfinished session: " unfinished)
      (reset! state/current-session (first unfinished)))))

