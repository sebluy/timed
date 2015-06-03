(ns bed-time.page
  (:require [bed-time.header :as header]))

(defn page [content]
  [:div.col-md-8.col-md-offset-2
   [header/header]
   content])


