(ns bed-time.sessions.form
  (:require [re-frame.core :refer [dispatch]]
            [bed-time.util :as util])
  (:require-macros [reagent.ratom :refer [reaction]]))

;(defonce start-field (reagent/atom {}))
;(defonce finish-field (reagent/atom {}))
;
;(defn enable []
;  (swap! state/state #(assoc % :update-form true)))
;
;(defn disable []
;  (swap! state/state #(assoc % :update-form false)))
;
;(defn new-session []
;  (reset! start-field (reagent/atom {}))
;  (reset! finish-field (reagent/atom {}))
;  (enable))
;
;(defn inject-session [[start finish]]
;  (reset! start-field
;          {:value start :text (.toLocaleString start)})
;  (reset! finish-field
;          {:value finish :text (some-> finish .toLocaleString)})
;  (enable))
;
;(defn update-field [field event value-fn error-fn]
;  (let [text (util/get-event-value event)
;        value (value-fn text)
;        error (error-fn text value)]
;    (reset! field {:text text :value value :error error})))
;
;(def update-time-field
;  (let [value-fn #(util/parse-datetime-str %)
;        error-fn #(if (util/datetime-invalid? %2) "Invalid Time")]
;    (fn [field event]
;      (update-field field event value-fn error-fn))))
;
;(defn session-form-valid? [session]
;  (every? #(and (not (get % :error)) (get % :value)) (vals session)))
;
;#_(defn update-session [activity]
;  (let [current-fields {:start @start-field :finish @finish-field}]
;    (if (session-form-valid? current-fields)
;      (let [{:keys [start] :as current-session}
;            (into {} (map #(update-in % [1] :value) current-fields))]
;        (if (contains? (@state/activities activity) start)
;          (sessions/update-session (merge {:activity activity} current-session))
;          (sessions/update-session (merge {:activity activity :new true}
;                                          current-session)))
;        (disable)))))
;

(defn- label [pre-label text]
  (let [value (util/str->date @text)
        error (if (util/datetime-invalid? value) "Invalid Time")
        date-str (util/date->str value)]
    [:label pre-label
     (if error
       [:span.label.label-danger error]
       [:span.label.label-success date-str])]))

(defn- input [key text]
  (println "Rerendering input")
  [:input.form-control
   {:type      "text"
    :value     @text
    :on-change #(dispatch
                 [:change-session-form-field
                  key
                  (util/get-event-value %)])}])

(defn- form-group [key text pre-label]
  (println "Re-rendering form-group")
  [:div.form-group
   [label pre-label text]
   [input key text]])

(defn- submit [event]
  (.preventDefault event)
  (dispatch [:submit-session-form]))

(defn edit-form [form-data]
  (let [start-text (reaction (get-in @form-data [:fields :start]))
        finish-text (reaction (get-in @form-data [:fields :finish]))]
    (println "Re-rendering form")
    [:form {:on-submit submit}
     [form-group :start start-text "Start: "]
     [form-group :finish finish-text "Finish: "]
     [:button.btn.btn-primary {:type "submit"} "Update"]
     [:button.btn.btn-danger
      {:type     "button"
       :on-click #(dispatch [:close-session-form])}
      "Cancel"]]))

