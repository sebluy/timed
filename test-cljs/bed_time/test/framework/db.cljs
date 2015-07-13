(ns bed-time.test.framework.db
  (:require [cljs.test :refer-macros [deftest is run-tests testing]]
            [goog.dom :as dom]
            [bed-time.framework.db :as db]
            [reagent.core :as reagent])
  (:require-macros [bed-time.macros :refer [with-subs]]))

(defn set-db [db]
  (db/transition (fn [_] db)))

(defn reset-subscriptions []
  (set! db/active-reactions {}))

(defn render [component]
  (reagent/render component (dom/getElement "app"))
  (reagent/flush))

; test components

(defn without-deref []
  (with-subs
    [test [:test]]
    (fn []
      [:div])))

(defn with-deref []
  (with-subs
    [test [:test]]
    (fn []
      [:div @test])))

(deftest test-subscriptions-deref
  (render [:div])
  (set-db {:test :value})
  (reset-subscriptions)
  (testing "undereferenced subscriptions should not create reactions"
    (render [without-deref])
    (is (nil? (db/active-reactions [:test]))))
  (testing "reactions should be removed on component removal"
    (render [with-deref])
    (is (some? (db/active-reactions [:test])))
    (render [:div])
    (is (nil? (db/active-reactions [:test])))))

(defn conditional-sub []
  (with-subs
    [cond [:cond]
     yes [:yes]
     no [:no]]
    (fn []
      (if @cond
        [:div @yes]
        [:div @no]))))

(deftest test-conditional-subscriptions
  (render [:div])
  (set-db {:cond true
           :yes  :yes
           :no   :no})
  (reset-subscriptions)
  ; add subscription reuse
  (testing "subscriptions are held only for active reactions"
    (render [conditional-sub])
    (is (some? (db/active-reactions [:cond])))
    (is (some? (db/active-reactions [:yes])))
    (is (nil? (db/active-reactions [:no])))
    (db/transition #(assoc % :cond false))
    (reagent/flush)
    (is (some? (db/active-reactions [:cond])))
    (is (nil? (db/active-reactions [:yes])))
    (is (some? (db/active-reactions [:no])))
    (db/transition #(assoc % :cond true))
    (reagent/flush)
    (is (some? (db/active-reactions [:cond])))
    (is (some? (db/active-reactions [:yes])))
    (is (nil? (db/active-reactions [:no])))))

(defn derived-query []
  (with-subs
    [test [:test]]
    (fn []
      (str @test " working?"))))

(defn derived-component []
  (with-subs
    [derived [:derived :test]]
    (fn []
      [:div#derived @derived])))

(deftest test-derived-subscriptions
  (render [:div])
  (set-db {:test "it's"})
  (reset-subscriptions)
  (db/register-derived-query [:derived :test] derived-query)
  (testing "derived subscriptions use existing reactions"
    (render [derived-component])
    (is (some? (db/active-reactions [:test])))
    (is (some? (db/active-reactions [:derived :test])))
    (is (= (dom/getTextContent (dom/getElement "derived")) "it's working?"))
    (render [:div])
    (is (nil? (db/active-reactions [:test])))
    (is (nil? (db/active-reactions [:derived :test])))))

;(run-tests)