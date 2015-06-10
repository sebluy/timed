(ns bed-time.sessions.sessions
  (:require [bed-time.state :as state]
            [ajax.core :as ajax]))

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

(defn delete [activity [start _ :as session]]
  (let [swap-fn (fn [activities]
                  (merge activities
                         {activity (dissoc (activities activity) start)}))
        handler (fn [_] (swap! state/activities swap-fn))]
    (ajax/POST "/delete-session" {:params          {:session session}
                                  :handler         handler
                                  :format          :edn
                                  :response-format :edn})))

(defn new-session [activity]
  (update-session
    {:activity activity :start (js/Date.) :finish nil :new true}))

(defn valid? [[_ finish]]
  (not (nil? finish)))

(defn time-spent [[start finish :as session]]
  (if (valid? session)
    (- (.getTime finish)
       (.getTime start))
    0))

