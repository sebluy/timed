(ns bed-time.sessions.sessions
  (:require [bed-time.state :as state]
            [ajax.core :as ajax]
            [bed-time.util :as util]
            [bed-time.sessions.current :as current]
            [re-frame.core :as re-frame]))

(defn sessions-map [] (sorted-map-by util/date-comparator))


(re-frame/register-handler
  :update-session
  (fn [db [_ {:keys [activity start finish]}]]
    (println "updating session")
    (assoc-in db [:activities activity start] finish)))

(re-frame/register-handler
  :post-update-session
  (fn [db [_ session]]
    (println "posting /update-session")
    (ajax/POST "/update-session"
               {:params          {:session session}
                :handler         #(re-frame/dispatch [:update-session session])
                :format          :edn
                :response-format :edn})
    db))

(re-frame/register-handler
  :new-session
  (fn [db [_ activity]]
    (re-frame/dispatch
      [:post-update-session
       {:activity activity :start (js/Date.) :finish nil :new true}])
    db))

(re-frame/register-handler
  :end-session
  (fn [db [_ {:keys [activity start]}]]
    (println "ending session")
    (println activity start)
    (re-frame/dispatch
      [:post-update-session
       {:activity activity :start start :finish (js/Date.) :new false}])
    db))

#_(defn delete [activity [start _ :as session]]

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
   (let [current-session (re-frame/subscribe [:current-session])]
     (fn []
       (println @current-session)
       [:input.btn.btn-sm.btn-danger
        {:type     "button"
         :class    class
         :value    (str "End " (@current-session :activity) " Session")
         :on-click #(re-frame/dispatch [:end-session @current-session])}]))))

#_(defn start-session-button [activity]
  [:input.btn.btn-sm.btn-success
   {:type     "button"
    :value    "Start Session"
    :on-click #(new-session activity)}])
