(ns bed-time.sessions.handlers
  (:require [ajax.core :refer [POST]]
            [re-frame.core :refer [register-handler dispatch path trim-v]]
            [bed-time.middleware :refer [static-db]]
            [bed-time.sessions.sessions :as sessions]))

(register-handler
  :receive-update-session
  (comp trim-v (path :activities))
  (fn [activities [{:keys [activity start] :as session}]]
    (if (sessions/current? session)
      (dispatch [:sync-tick]))
    (assoc-in activities [activity start] session)))

(register-handler
  :post-update-session
  (fn [db [_ session]]
    (POST "/update-session"
          {:params          {:session session}
           :handler         #(dispatch [:receive-update-session session])
           :format          :edn
           :response-format :edn})
    db))

(register-handler
  :update-session
  static-db
  (fn [[session]]
    (dispatch [:post-update-session session])))

(register-handler
  :start-session
  static-db
  (fn [[activity]]
    (dispatch
      [:update-session
       {:activity activity :start (js/Date.) :finish nil :new true}])))

(register-handler
  :finish-session
  static-db
  (fn [[{:keys [activity start]}]]
    (dispatch
      [:update-session
       {:activity activity :start start :finish (js/Date.) :new false}])))

(register-handler
  :receive-delete-session
  (comp trim-v (path :activities))
  (fn [activities [{:keys [activity start]}]]
    (if (= (count (activities activity)) 1)
      (dissoc activities activity)
      (update-in activities [activity] #(dissoc % start)))))

(register-handler
  :delete-session
  static-db
  (fn [[session]]
    (POST "/delete-session"
          {:params         {:session session}
           :handler        #(dispatch [:receive-delete-session session])
           :format         :edn
           :reponse-format :edn})))

(register-handler
  :recieve-swap-session
  (comp trim-v (path :activities))
  (fn [activities [new-session old-session]]
    (update-in activities [(new-session :activity)]
               #(merge (dissoc % (old-session :start))
                       {(new-session :start) (new-session :finish)}))))

(register-handler
  :swap-session
  static-db
  (fn [[new-session old-session]]
    (POST "/swap-session"
          {:params         {:old-session old-session
                            :new-session new-session}
           :handler        #(dispatch
                             [:recieve-swap-session new-session old-session])
           :format         :edn
           :reponse-format :edn})))

