(ns bed-time.navbar
  (:require [bed-time.days :as days]))

(defn go-to-bed []
  (days/update-day {:bed-time (js/Date.) :new true}))

(defn go-to-bed-button []
  [:input.btn.btn-large.btn-success.navbar-btn
   {:type     "button"
    :value    "Go to bed!"
    :on-click #(go-to-bed)}])

(defn wake-up []
  (days/update-day {:bed-time     (first (first @days/days))
                    :wake-up-time (js/Date.)}))

(defn wake-up-button []
  [:input.btn.btn-large.btn-info.navbar-btn
   {:type     "button"
    :value    "Wake Up!"
    :on-click #(wake-up)}])

(println (days/valid? nil))

(defn wake-or-sleep-button []
  (let [current-days @days/days]
    (if (or (empty? current-days) (days/valid? (first current-days)))
      (go-to-bed-button)
      (wake-up-button))))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:href "/#list"} "Bed Time!"]]
    [:ul.nav.navbar-nav
     [:li [:a {:href "/#list"} "List"]]
     [:li [:a {:href "/#time-slept-plot"} "Time Slept Plot"]]
     [:li [:a {:href "/#bed-time-plot"} "Bed Time Plot"]]
     [:li [:a {:href "/#wake-up-time-plot"} "Wake Up Time Plot"]]]
    [:ul.nav.navbar-nav.navbar-right
     [:li [wake-or-sleep-button]]]]])
