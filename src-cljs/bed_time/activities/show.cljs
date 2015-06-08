(ns bed-time.activities.show)

(defn page [params]
  (fn []
    [:div.col-md-8.col-md-offset-2
     [:h1 "Fuck Yeah " (params :activity)]]))

