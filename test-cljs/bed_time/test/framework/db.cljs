(ns bed-time.test.framework.db
  (:require [cljs.test :refer-macros [deftest is run-tests]]))

(deftest
  test-numbers
  (is (= "test" "test")))

(run-tests)
