(ns scoring-engine-test
  (:require [clojure.test :refer :all]
            [scoring-engine :refer :all]))

(deftest test-calculate-points
  (testing "Perfect answer with full time bonus"
    (is (= 2000 (calculate-points :perfect 60000 60000 0))))

  (testing "Perfect answer with half of the time remaining"
    (is (= 1500 (calculate-points :perfect 30000 60000 0))))

  (testing "Close answer"
    (is (= 500 (calculate-points :close 50000 60000 0)) "Close answers do not receive a time bonus"))

  (testing "Testing the Streak multiplier"
    (is (= 3000 (calculate-points :perfect 30000 60000 10)) "A 10x streak should double the points")))

(deftest test-update-streak
  (testing "Increasing the streak"
    (is (= 1 (update-streak 0 :perfect)))
    (is (= 6 (update-streak 5 :perfect))))

  (testing "Resetting the streak"
    (is (= 0 (update-streak 5 :close)) "A close answer should reset the streak")
    (is (= 0 (update-streak 10 :wrong)) "A wrong answer should reset the streak")))