(ns bed-time.sessions.handlers
  (:require [ajax.core :refer [POST]]
            [re-frame.core :refer [register-handler dispatch]]
            [bed-time.util :as util]))

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
                (merge {:activity activity
                        :fields {:start  (util/date->str start)
                                 :finish (util/date->str finish)}}
                       (if new
                         {:new true}
                         {:new false :old-session session})))))

  (register-handler
    :change-session-form-field
    (fn [db [_ key text]]
      (assoc-in db [:edit-session-form :fields key] text)))

  (register-handler
    :submit-session-form
    (fn [db _]
      (let [{:keys [activity new old-session fields]} (db :edit-session-form)]
        (if (not new)
          (dispatch [:delete-session old-session]))
        (dispatch [:update-session
                   {:activity activity
                    :start    (util/str->date (fields :start))
                    :finish   (util/str->date (fields :finish))
                    :new      true}]))
      db))

  (register-handler
    :close-session-form
    (fn [db _]
      (dissoc db :edit-session-form))))



