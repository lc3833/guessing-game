(ns game-logic-test
  (:require [clojure.test :refer :all]
            [game-logic :refer :all]))

(deftest test-levenshtein
  (testing "Basic functionality of Levenshtein distance"
    (is (= 0 (levenshtein "tiger" "tiger")) "Identical words must have distance 0")
    (is (= 1 (levenshtein "tiger" "tigerh")) "One extra letter = distance 1")
    (is (= 1 (levenshtein "tiger" "tigr")) "One missing letter = distance 1")
    (is (= 1 (levenshtein "tiger" "tager")) "One wrong letter = distance 1"))

  (testing "Testing empty strings"
    (is (= 0 (levenshtein "" "")))
    (is (= 5 (levenshtein "tiger" ""))))

  (testing "Case sensitivity (Upper/Lower case)"
    (is (= 0 (levenshtein "TigEr" "tiger")))))

(deftest test-judge-answer
  (testing "Perfect answers"
    (is (= :perfect (judge-answer "giraffe" "giraffe")))
    (is (= :perfect (judge-answer "   giraffe   " "giraffe")) "Should ignore whitespace (trim)"))

  (testing "Close answers"
    (is (= :close (judge-answer "girrafe" "giraffe")) "One typo")
    (is (= :close (judge-answer "girafe" "giraffe")) "Missing one letter"))

  (testing "Wrong answers"
    (is (= :wrong (judge-answer "lion" "giraffe")))
    (is (= :wrong (judge-answer "giraffffff" "giraffe")) "Too many errors")))

(deftest test-mask-solution
  (testing "Masking unrevealed letters"
    (is (= "_______" (mask-solution "giraffe" #{})) "All letters must be hidden")
    (is (= "g______" (mask-solution "giraffe" #{\g})) "Only g should be visible")
    (is (= "_i_a___" (mask-solution "giraffe" #{\i \a})) "Repeating letters should be visible everywhere"))

  (testing "Special characters"
    (is (= "_ _" (mask-solution "a b" #{})) "Space must always be visible!")))