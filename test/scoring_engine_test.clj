(ns scoring-engine-test
  (:require [clojure.test :refer :all]
            [scoring-engine :refer :all]))

(deftest test-calculate-points
  (testing "Savrsen odgovor (Perfect) sa punim vremenom"
    (is (= 2000 (calculate-points :perfect 60000 60000 0))))

  (testing "Savrsen odgovor na pola vremena"
    (is (= 1500 (calculate-points :perfect 30000 60000 0))))

  (testing "Blizu odgovor (Close)"
    (is (= 500 (calculate-points :close 50000 60000 0)) "Blizu odgovor nema time bonus"))

  (testing "Testiranje Streak mnozioca"
    (is (= 3000 (calculate-points :perfect 30000 60000 10)) "Streak od 10 treba da duplira poene")))

(deftest test-update-streak
  (testing "Povecanje niza (Streak)"
    (is (= 1 (update-streak 0 :perfect)))
    (is (= 6 (update-streak 5 :perfect))))

  (testing "Resetovanje niza"
    (is (= 0 (update-streak 5 :close)) "Blizu odgovor treba da resetuje streak")
    (is (= 0 (update-streak 10 :wrong)) "Pogresan odgovor treba da resetuje streak")))