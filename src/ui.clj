(ns ui
  (:require [clojure.string :as str]))

(defn render-art [art-lines category]
  (println "\n------------------------------------------")
  (println "CATEGORY:" (str/upper-case (name category)))
  (println "------------------------------------------")
  (doseq [line art-lines]
    (println line))
  (println "------------------------------------------"))

(defn select-category []
  (loop []
    (println "\nSelect a category:")
    (println "1. NATURE (Animals, nature, planets)")
    (println "2. OBJECTS (Tools, items, logos)")
    (println "3. PLACES (Buildings, maps, monuments)")
    (println "4. MIX (Hardcore mode - Everything)")
    (print "Enter number (1-4): ") (flush)

    (let [choice (str/trim (read-line))]
      (case choice
        "1" :nature
        "2" :objects
        "3" :places
        "4" :mix
        (do (println "\n❌ Invalid input! Please enter 1, 2, 3 or 4.")
            (recur))))))

(defn select-difficulty []
  (loop []
    (println "\nSelect difficulty:")
    (println "1. EASY     (5 minutes, 3 lives, 3 hints)")
    (println "2. MEDIUM   (2 minutes, 2 lives, 2 hints)")
    (println "3. HARD     (60 seconds, 1 life, 1 hint)")
    (println "4. HARDCORE (30 seconds, 0 lives, NO HINTS!)")
    (print "Enter number (1-4): ") (flush)

    (let [choice (str/trim (read-line))]
      (case choice
        "1" {:name :easy     :time (* 5 60 1000)}
        "2" {:name :medium   :time (* 2 60 1000)}
        "3" {:name :hard     :time (* 60 1000)}
        "4" {:name :hardcore :time (* 30 1000)}
        (do (println "\n❌ Invalid input! Please enter 1, 2, 3 or 4.")
            (recur))))))

(defn print-welcome []
  (println "******************************************")
  (println "******* GUESS THE ASCII - ULTIMATE *******")
  (println "******************************************"))

(defn print-game-over [score]
  (println "\n--- GAME OVER ---")
  (println "Final Score:" score "points.")
  (println "Thanks for playing!"))

(defn print-high-scores [scores]
  (println "\n==========================================")
  (println "       🏆 HALL OF FAME (TOP 5) 🏆       ")
  (println "==========================================")
  (println (format "%-5s %-15s %-10s" "RANK" "PLAYER" "SCORE"))
  (println "------------------------------------------")
  (doseq [[idx entry] (map-indexed vector scores)]
    (println (format "%-5d %-15s %-10d"
                     (inc idx)
                     (:name entry)
                     (:score entry))))
  (println "==========================================\n"))

(defn print-full-leaderboard [all-scores]
  (println "\n==========================================")
  (println "           🏆 HALL OF FAME 🏆           ")
  (println "==========================================")

  (doseq [diff [:easy :medium :hard :hardcore]]
    (let [scores (get all-scores diff [])]
      (println (str "\n      HIGH SCORES (" (str/upper-case (name diff))")"))
      (println "")
      (println (format "%-5s %-15s %-10s" "RANK" "PLAYER" "SCORE"))
      (println "------------------------------------------")
      (if (empty? scores)
        (println "  (No scores yet)")
        (doseq [[idx entry] (map-indexed vector scores)]
          (println (format "%-5d %-15s %-10d"
                           (inc idx)
                           (:name entry)
                           (:score entry)))))))
  (println "\n=========================================="))