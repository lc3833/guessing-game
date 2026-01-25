(ns db-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [db :refer :all]
            [ui]))

(defn with-temp-files [f]
  (with-redefs [db/load-high-scores (constantly {:easy [] :medium [] :hard [] :hardcore []})
                db/save-high-score  (fn [_ _ _] [])
                db/load-achievements (constantly {})]
    (f)))

(use-fixtures :each with-temp-files)

(deftest test-load-database
  (testing "Loading existing file"
    (let [data (load-database "drawings.edn")]
      (is (not (empty? data)) "Database must not be empty")
      (is (vector? data) "Database must be a vector of maps")
      (is (:id (first data)) "Every puzzle must have an ID")
      (is (:solution (first data)) "Every puzzle must have a solution")))

  (testing "Loading non-existent file"
    (is (empty? (load-database "non_existent_file.edn")) "Must return empty vector for bad file")))

(deftest test-get-puzzles-for-mode
  (let [dummy-db [{:id 1 :category :nature}
                  {:id 2 :category :objects}
                  {:id 3 :category :nature}]]
    (testing "Filtering by category"
      (is (= 2 (count (get-puzzles-for-mode dummy-db :nature))))
      (is (= 1 (count (get-puzzles-for-mode dummy-db :objects)))))

    (testing "Mix mode returns everything"
      (is (= 3 (count (get-puzzles-for-mode dummy-db :mix)))))))

(deftest test-high-score-logic
  (let [current-scores [{:name "Player1" :score 100}
                        {:name "Player2" :score 200}]]
    (testing "Adding a new score"
      (let [new-score {:name "Player3" :score 300}
            updated (reverse (sort-by :score (conj current-scores new-score)))]
        (is (= "Player3" (:name (first updated))) "Highest score must be first")
        (is (= 300 (:score (first updated))))))))

(deftest test-format-achievements
  (testing "Text formatting"
    (let [data {"cveks" #{:speed-demon :nature-lover}}
          output (format-achievements-for-txt data)]
      (is (clojure.string/includes? output "PLAYER: cveks"))
      (is (clojure.string/includes? output "Speed Demon")))))

(deftest preview-all-drawings-test
  (testing "VISUAL CHECK: Display all drawings from DB at once"
    (let [db (load-database "drawings.edn")]
      (println "\n" (apply str (repeat 40 "=")))
      (println "--- ALL DRAWINGS GALLERY ---")
      (println (apply str (repeat 40 "=")))

      (doseq [drawing db]
        (println (str "\nID: " (:id drawing) " | Name: " (:name drawing) " | Cat: " (:category drawing)))
        (println (apply str (repeat 20 "-")))
        (ui/render-art (:art drawing) (:category drawing))
        (println (apply str (repeat 20 "-"))))

      (println "\n" (apply str (repeat 40 "=")))
      (println "TOTAL DRAWINGS IN DB: " (count db))
      (println (apply str (repeat 40 "=")))
      (is (pos? (count db)) "Database must contain images to display"))))