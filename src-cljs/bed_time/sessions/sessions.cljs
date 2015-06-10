(ns bed-time.sessions.sessions
  (:require [bed-time.state :as state]
            [ajax.core :as ajax]))

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

(defn update-session [{:keys [activity start finish] :as session}]
  (let [handler (fn [_]
                  (swap! state/activities
                         #(assoc-in % [activity start] finish))
                  (if (nil? finish)
                    (reset! state/current-session session)))]
    (ajax/POST "/update-session" {:params {:session session}
                                  :handler handler
                                  :format :edn
                                  :response-format :edn})))

(defn end-current []
  (let [session (merge @state/current-session {:new false :finish (js/Date.)})]
    (update-session session)
    (reset! state/current-session nil)))

(defn delete [activity [start _ :as session]]
  (let [swap-fn (fn [activities]
                  (merge activities
                         {activity (dissoc (activities activity) start)}))
        handler (fn [_] (swap! state/activities swap-fn))]
    (ajax/POST "/delete-session" {:params          {:session session}
                                  :handler         handler
                                  :format          :edn
                                  :response-format :edn})))

(defn valid? [[_ finish]]
  (not (nil? finish)))

(defn time-spent [[start finish :as session]]
  (if (valid? session)
    (- (.getTime finish)
       (.getTime start))
    0))

