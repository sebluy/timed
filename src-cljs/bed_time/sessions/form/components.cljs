(ns bed-time.sessions.form.components
  (:require [re-frame.core :refer [dispatch dispatch-sync]]
            [bed-time.util :as util])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn- label [pre-label text]
  (println "Rerendering label")
  (let [value (util/str->date @text)
        error (if (util/datetime-invalid? value) "Invalid Time")
        date-str (util/date->str value)]
    [:label pre-label
     (if value
       (if error
         [:span.label.label-danger error]
         [:span.label.label-success date-str]))]))

(defn- input [key text]
  (println "Rerendering input")
  [:input.form-control
   {:type      "text"
    :value     @text
    :on-change #(dispatch-sync
                 [:change-session-form-field key (util/get-event-value %)])}])

(defn- form-group [key text pre-label]
  (println "Rerendering " key " form group")
  [:div.form-group
   [label pre-label text]
   [input key text]])

(defn- submit [event]
  (.preventDefault event)
  (dispatch [:submit-session-form]))

(defn edit-form [form-reaction]
  (let [start-text (reaction (println "Refreshing start-text")
                             (get-in @form-reaction [:fields :start]))
        finish-text (reaction (println "Refreshing finish-text")
                              (get-in @form-reaction [:fields :finish]))]
    (fn []
      (println "Rerendering edit form")
      [:form {:on-submit submit}
       [form-group :start start-text "Start: "]
       [form-group :finish finish-text "Finish: "]
       [:div.btn-toolbar
        [:button.btn.btn-primary {:type "submit"} "Update"]
        [:button.btn.btn-danger
         {:type     "button"
          :on-click #(dispatch [:close-session-form])}
         "Cancel"]]])))

(let [test-ratom (reagent.ratom/atom {:hi {:my 4}})
      test-reaction (reaction
                      (println "refreshing reaction")
                      (println ((get-in @test-ratom [:hi :my]))))]
  @test-reaction
  (swap! test-ratom #(assoc-in % [:hi :you] 5))
  @test-reaction)

(identical? 4 4)

