(ns bed-time.sessions.handlers
  (:require [ajax.core :refer [POST]]
            [re-frame.core :refer [register-handler dispatch]]))

(defn- date-to-field [date]
  {:value date :string (some-> date .toLocaleString) :error nil})

(defn register []
  (register-handler
    :receive-update-session
    (fn [db [_ {:keys [activity start finish]}]]
      (assoc-in db [:activities activity start] finish)))

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
    (fn [db [_ session]]
      (dispatch [:post-delete-session session])
      db))

  (register-handler
    :receive-delete-session
    (fn [db [_ {:keys [activity start]}]]
      (if (= (count (get-in db [:activities activity])) 1)
        (update-in db [:activities] #(dissoc % activity))
        (update-in db [:activities activity] #(dissoc % start)))))

  (register-handler
    :post-delete-session
    (fn [db [_ session]]
      (POST "/delete-session"
            {:params         {:session session}
             :handler        #(dispatch [:receive-delete-session session])
             :format         :edn
             :reponse-format :edn})
      db))

  (register-handler
    :edit-session
    (fn [db [_ {:keys [activity start finish new] :as session}]]
      (assoc db :edit-session-form
                (merge {:fields {:start  (date-to-field start)
                                 :finish (date-to-field finish)}}
                       (if new
                         {:new true :activity activity}
                         {:new false :old-session session}))))))

