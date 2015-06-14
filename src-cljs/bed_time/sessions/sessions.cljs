(ns bed-time.sessions.sessions
  (:require [bed-time.state :as state]
            [ajax.core :as ajax]
            [bed-time.util :as util]
            [bed-time.sessions.current :as current]))

(defn sessions-map [] (sorted-map-by util/date-comparator))

(defn update-session [{:keys [activity start finish] :as session}]
  (let [handler (fn [_]
                  (current/update-current-session session)
                  (swap! state/activities
                         #(assoc-in % [activity start] finish)))]
    (ajax/POST "/update-session" {:params          {:session session}
                                  :handler         handler
                                  :format          :edn
                                  :response-format :edn})))

(defn delete [activity [start _ :as session]]
  (let [swap-fn (fn [activities]
                  (if (= (count (activities activity)) 1)
                    (dissoc activities activity)
                    (merge activities
                           {activity (dissoc (activities activity) start)})))
        handler (fn [_] (swap! state/activities swap-fn))]
    (ajax/POST "/delete-session" {:params          {:session session}
                                  :handler         handler
                                  :format          :edn
                                  :response-format :edn})))

(defn end-current []
  (let [session (merge @state/current-session {:new false :finish (js/Date.)})]
    (update-session session)))

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

(defn end-session-button
  ([] (end-session-button nil))
  ([class]
   [:input.btn.btn-sm.btn-danger
    {:type     "button"
     :class    class
     :value    (str "End " (@state/current-session :activity) " Session")
     :on-click #(end-current)}]))

(defn start-session-button [activity]
  [:input.btn.btn-sm.btn-success
   {:type     "button"
    :value    "Start Session"
    :on-click #(new-session activity)}])
