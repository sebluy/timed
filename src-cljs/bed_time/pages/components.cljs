(ns bed-time.pages.components)

(defn pending-button [class]
  [:input.btn
   {:type  "button"
    :class (str class " btn-warning")
    :value "Pending"}])
