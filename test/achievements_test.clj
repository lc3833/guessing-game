(ns achievements-test
  (:require [clojure.test :refer :all]
            [achievements :refer :all]))

(def empty-game-state {:streak 0 :difficulty-mode :easy :lives 3})
(def empty-round-data {:result :wrong :time-left 0 :total-time 60000 :hints-used 0 :points 0})
(def empty-stats {:solved-ids #{} :category-counts {} :total-score 0 :no-hint-count 0 :sessions 1})

(deftest test-category-achievements
  (testing "Nature Lover (5 Nature puzzles)"
    (let [stats (assoc empty-stats :category-counts {:nature 5})]
      (is (contains? (evaluate empty-game-state empty-round-data stats 100) :nature-lover))))

  (testing "Tech Wizard (5 Objects puzzles)"
    (let [stats (assoc empty-stats :category-counts {:objects 5})]
      (is (contains? (evaluate empty-game-state empty-round-data stats 100) :tech-wizard))))

  (testing "Nema achievementa ako nije dovoljno reseno"
    (let [stats (assoc empty-stats :category-counts {:nature 4})]
      (is (not (contains? (evaluate empty-game-state empty-round-data stats 100) :nature-lover))))))

(deftest test-speed-achievements
  (testing "Speed Demon (< 5 sekundi)"
    (let [round (assoc empty-round-data :result :perfect :total-time 60 :time-left 56)]
      (is (contains? (evaluate empty-game-state round empty-stats 100) :speed-demon))))

  (testing "Sonic (< 2 sekunde)"
    (let [round (assoc empty-round-data :result :perfect :total-time 60 :time-left 59)]
      (is (contains? (evaluate empty-game-state round empty-stats 100) :sonic)))))

(deftest test-streak-achievements
  (testing "Streak Master (5x)"
    (let [state (assoc empty-game-state :streak 5)]
      (is (contains? (evaluate state empty-round-data empty-stats 100) :streak-master))))

  (testing "Double Digits (10x)"
    (let [state (assoc empty-game-state :streak 10)]
      (is (contains? (evaluate state empty-round-data empty-stats 100) :double-digits)))))

(deftest test-point-achievements
  (testing "High Roller (> 3000 poena u rundi)"
    (let [round (assoc empty-round-data :points 3500)]
      (is (contains? (evaluate empty-game-state round empty-stats 100) :high-roller))))

  (testing "Centurion (Ukupan skor > 100.000)"
    (let [stats (assoc empty-stats :total-score 100001)]
      (is (contains? (evaluate empty-game-state empty-round-data stats 100) :centurion)))))

(deftest test-time-of-day
  (testing "Night Owl (Izmedju 00:00 i 05:00)"
    (with-redefs [achievements/get-current-hour (constantly 3)]
      (is (contains? (evaluate empty-game-state empty-round-data empty-stats 100) :night-owl))))

  (testing "Early Bird (Izmedju 05:00 i 08:00)"
    (with-redefs [achievements/get-current-hour (constantly 6)]
      (is (contains? (evaluate empty-game-state empty-round-data empty-stats 100) :early-bird))))

  (testing "Nema achievementa u podne"
    (with-redefs [achievements/get-current-hour (constantly 12)]
      (let [result (evaluate empty-game-state empty-round-data empty-stats 100)]
        (is (not (contains? result :night-owl)))
        (is (not (contains? result :early-bird)))))))

(deftest test-completionist
  (testing "The Completionist"
    (let [stats (assoc empty-stats :solved-ids (set (range 100)))]
      (is (contains? (evaluate empty-game-state empty-round-data stats 100) :completionist)))))