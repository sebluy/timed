(ns bed-time.sessions.current
  (:require [bed-time.state :as state]
            [ajax.core :as ajax]
            [bed-time.sessions.sessions :as sessions]))

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

(defn end-current []
  (let [session (merge @state/current-session {:new false :finish (js/Date.)})]
    (sessions/update-session session)
    (reset! state/current-session nil)))
