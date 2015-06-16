(ns bed-time.sessions.form
  (:require [reagent.core :as reagent]
            [bed-time.util :as util]
            [bed-time.sessions.sessions :as sessions]))

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
;(defn error-label [error alternate]
;  (if error
;    [:span.label.label-danger error]
;    [:span.label.label-success alternate]))
;
;(defn text-input [field pre-label label-fn update-fn]
;  (let [{:keys [text value error]} @field
;        label (label-fn text value error)]
;    [:div.form-group
;     [:label pre-label (error-label error label)]
;     [:input {:type      "text"
;              :class     "form-control"
;              :value     text
;              :on-change #(update-fn field %)}]]))
;
;(def datetime-input
;  (let [label-fn #(some-> %2 .toLocaleString)]
;    (fn [field pre-label]
;      (text-input field pre-label label-fn update-time-field))))
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
;#_(defn update-form [activity]
;  [:form
;   [datetime-input start-field "Start: "]
;   [datetime-input finish-field "Finish: "]
;   [:input.btn.btn-primary
;    {:type     "button"
;     :value    "Update"
;     :on-click #(update-session activity)}]
;   [:input.btn.btn-danger
;    {:type     "button"
;     :value    "Cancel"
;     :on-click #(disable)}]])

