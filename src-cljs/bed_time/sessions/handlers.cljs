(ns bed-time.sessions.handlers
  (:require [ajax.core :refer [POST]]
            [re-frame.core :refer [register-handler dispatch]]))

(defn register []
  (register-handler
    :recieve-update-session
    (fn [db [_ {:keys [activity start finish]}]]
      (assoc-in db [:activities activity start] finish)))

  (register-handler
    :post-update-session
    (fn [db [_ session]]
      (POST "/update-session"
            {:params          {:session session}
             :handler         #(dispatch [:update-session session])
             :format          :edn
             :response-format :edn})
      db))

  (register-handler
    :update-session
    (fn [db [_ session]]
      (dispatch [:post-update-session session])
      db))

  (register-handler
    :new-session
    (fn [db [_ activity]]
      (dispatch
        [:update-session
         {:activity activity :start (js/Date.) :finish nil :new true}])
      db))

  (register-handler
    :end-session
    (fn [db [_ {:keys [activity start]}]]
      (dispatch
        [:update-session
         {:activity activity :start start :finish (js/Date.) :new false}])
      db))

  (register-handler
    :delete-session
    (fn [db [_ {:keys [activity start]}]]
      (if (= (count (get-in db [:activities activity])) 1)
        (update-in db [:activities] #(dissoc activity))
        (update-in db [:activities activity] #(dissoc start)))))

  (register-handler
    :receive-delete-session
    (fn [db [_ session]]
      (dispatch [:delete-session session])
      db))

  (register-handler
    :post-delete-session
    (fn [db [_ session]]
      (POST "/delete-session"
            {:params         {:session session}
             :handler        #(dispatch [:recieve-delete-session session])
             :format         :edn
             :reponse-format :edn})
      db)))
