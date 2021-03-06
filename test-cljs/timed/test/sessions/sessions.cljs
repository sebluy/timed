(ns timed.test.framework.db
  (:require [cljs.test :refer-macros [deftest is run-tests testing]]
            [timed.sessions.sessions :as sessions]))

(deftest test-pending
  (testing "should find pending sessions"
    (let [not-pending {:start :fake-date :finish nil}
          pending (assoc not-pending :pending true)
          activities {:activity-1 {:sessions {:0 pending
                                              :1 not-pending
                                              :2 pending}}
                      :activity-2 {:sessions {:0 not-pending
                                              :1 pending}}}]
      (is (= (sessions/pending activities) (list pending pending pending)))))
  (testing "should return an empty list if none"
    (let [not-pending {:start :fake-date :finish nil}
          activities {:activity-1 {:sessions {:1 not-pending}}
                      :activity-2 {:sessions {:0 not-pending}}}]
      (is (= (sessions/pending activities) (list))))))


;(run-tests)
