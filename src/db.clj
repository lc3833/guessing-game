(ns db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]
            [achievements]))

(defn load-database [filename]
  (try
    (if-let [res (io/resource filename)]
      (edn/read-string (slurp res))
      (let [f (io/file filename)]
        (if (.exists f)
          (edn/read-string (slurp f))
          [])))
    (catch Exception e
      (println "ERROR loading database:" (.getMessage e))
      [])))

(defn get-puzzles-for-mode [db mode]
  (if (= mode :mix)
    db
    (filter #(= (:category %) mode) db)))

(defn load-high-scores []
  (try
    (let [f (io/file "highscores.edn")]
      (if (.exists f) (edn/read-string (slurp f))
                      {:easy [] :medium [] :hard [] :hardcore []}))
    (catch Exception e {:easy [] :medium [] :hard [] :hardcore []})))

(defn format-scores-for-txt [all-scores difficulty]
  (let [scores (get all-scores difficulty)
        sb (StringBuilder.)]
    (.append sb (str "\n      HIGH SCORES (" (str/upper-case (name difficulty)) ") \n\n"))
    (.append sb (format "%-5s %-15s %-10s\n" "RANK" "PLAYER" "SCORE"))
    (.append sb "------------------------------------------\n")
    (doseq [[idx entry] (map-indexed vector scores)]
      (.append sb (format "%-5d %-15s %-10d\n" (inc idx) (:name entry) (:score entry))))
    (.append sb "==========================================\n")
    (.toString sb)))

(defn save-high-score [nickname score difficulty]
  (try
    (let [all-scores (load-high-scores)
          current-list (get all-scores difficulty [])
          new-list (take 5 (reverse (sort-by :score (conj current-list {:name nickname :score score}))))
          updated-all-scores (assoc all-scores difficulty new-list)]
      (spit "highscores.edn" (pr-str updated-all-scores))
      (let [txt-output (str (format-scores-for-txt updated-all-scores :easy)
                            (format-scores-for-txt updated-all-scores :medium)
                            (format-scores-for-txt updated-all-scores :hard)
                            (format-scores-for-txt updated-all-scores :hardcore))]
        (spit "highscores.txt" txt-output))
      new-list)
    (catch Exception e (println "Error saving highscore:" (.getMessage e)) [])))

(defn load-stats []
  (try
    (let [f (io/file "stats.edn")]
      (if (.exists f) (edn/read-string (slurp f)) {}))
    (catch Exception _ {})))

(defn update-player-stats [nickname puzzle-id category points hints-used]
  (let [all-stats (load-stats)
        raw-user-stats (get all-stats nickname {})

        default-stats {:solved-ids #{}
                       :category-counts {}
                       :total-score 0
                       :no-hint-count 0
                       :sessions 0}

        user-stats (merge default-stats raw-user-stats)
        new-solved-ids (conj (:solved-ids user-stats) puzzle-id)
        new-cat-counts (update (:category-counts user-stats) category (fnil inc 0))
        new-total-score (+ (:total-score user-stats) points)
        new-no-hint (if (zero? hints-used)
                      (inc (:no-hint-count user-stats))
                      (:no-hint-count user-stats))
        updated-user-stats (assoc user-stats
                             :solved-ids new-solved-ids
                             :category-counts new-cat-counts
                             :total-score new-total-score
                             :no-hint-count new-no-hint)
        final-stats (assoc all-stats nickname updated-user-stats)]
        (spit "stats.edn" (pr-str final-stats))
        updated-user-stats))

(defn increment-session [nickname]
  (let [all-stats (load-stats)
        raw-user-stats (get all-stats nickname {})
        user-stats (merge {:sessions 0} raw-user-stats)
        updated-user-stats (update user-stats :sessions inc)
        final-stats (assoc all-stats nickname updated-user-stats)]
    (spit "stats.edn" (pr-str final-stats))))


(defn load-achievements []
  (try
    (let [f (io/file "achievements.edn")]
      (if (.exists f) (edn/read-string (slurp f)) {}))
    (catch Exception _ {})))

(defn format-achievements-for-txt [all-data]
  (let [sb (StringBuilder.)]
    (.append sb "==========================================\n")
    (.append sb "       🏆 HALL OF ACHIEVEMENTS 🏆       \n")
    (.append sb "==========================================\n")
    (doseq [[player unlocked-set] (sort-by key all-data)]
      (.append sb (str "\n👤 PLAYER: " player "\n"))
      (.append sb "------------------------------------------\n")
      (if (empty? unlocked-set)
        (.append sb "  (No achievements yet)\n")
        (doseq [ach-key (sort unlocked-set)]
          (if-let [info (get achievements/criteria ach-key)]
            (.append sb (format "  %-20s | %s\n" (:name info) (:desc info)))
            (.append sb (str "  " ach-key "\n"))))))
    (.append sb "\n==========================================\n")
    (.toString sb)))

(defn save-new-achievements [nickname new-unlocked-set]
  (try
    (let [all-data (load-achievements)
          existing-set (get all-data nickname #{})
          updated-set (set/union existing-set new-unlocked-set)
          truly-new (set/difference new-unlocked-set existing-set)
          final-data (assoc all-data nickname updated-set)]
      (spit "achievements.edn" (pr-str final-data))
      (spit "achievements.txt" (format-achievements-for-txt final-data))
      truly-new)
    (catch Exception e #{})))

(defn get-achievements-for-player [nickname]
  (let [all-data (load-achievements)
        user-achievements (get all-data nickname #{})]
    (if (empty? user-achievements)
      (str "\nPlayer '" nickname "' has no achievements yet.\n")
      (let [sb (StringBuilder.)]
        (.append sb (str "\n🏆 ACHIEVEMENTS FOR: " nickname " 🏆\n"))
        (.append sb "==========================================\n")
        (doseq [ach-key (sort user-achievements)]
          (if-let [info (get achievements/criteria ach-key)]
            (.append sb (format "✅ %-20s | %s\n" (:name info) (:desc info)))
            (.append sb (str "✅ " ach-key "\n"))))
        (.append sb "==========================================\n")
        (.toString sb)))))