(ns timed.test.framework.db
  (:require [cljs.test :refer-macros [deftest is run-tests testing]]
            [timed.framework.db :as db]
            [reagent.core :as reagent]
            [goog.dom :as dom]
            [timed.sessions.transitions :as session-transitions]))

(defn set-db [db]
  (db/transition (fn [_] db)))

(defn reset-subscriptions []
  (set! db/active-reactions {}))

(defn render [component]
  (reagent/render component (dom/getElement "app"))
  (reagent/flush))

(deftest test-update-session
  (let [old-session {:start :faker-date :activity "test"}
        new-session {:start :working :activity "test"}]
    (reset-subscriptions)
    (render [:div])
    (set-db {:activities {"test" {:sessions {:fake-date old-session}}}})
    (db/transition (session-transitions/update-session old-session new-session))
    (is (= (db/query-once [:activities "test" :sessions :working]) new-session))
    (is (nil? (db/query-once [:activites "test" :sessions :faker-date])))))

;(run-tests)
