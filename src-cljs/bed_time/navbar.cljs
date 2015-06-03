(ns bed-time.navbar)

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:href "/#list"} "Bed Time!"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      [:li [:a {:href "/#list"} "List"]]
      [:li [:a {:href "/#plot"} "Plot"]]]]]])
