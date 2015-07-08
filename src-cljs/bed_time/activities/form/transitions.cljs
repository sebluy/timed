(ns bed-time.activities.form.transitions)

(defn update-field [text]
  (fn [db]
    (assoc-in db [:page :activity-form :field] text)))

