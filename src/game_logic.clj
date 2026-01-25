(ns game-logic
  (:require [clojure.string :as str])
  (:import [java.util Random Collections]))

(defn levenshtein [s1 s2]
  (let [s1 (str/lower-case s1)
        s2 (str/lower-case s2)
        v0 (vec (range (inc (count s2))))]
    (loop [s1 s1 v0 v0]
      (if (empty? s1)
        (peek v0)
        (let [prev-v0 v0
              c1 (first s1)]
          (recur (rest s1)
                 (reduce (fn [v i]
                           (let [c2 (nth s2 (dec i))
                                 cost (if (= c1 c2) 0 1)
                                 del (inc (nth prev-v0 i))
                                 ins (inc (peek v))
                                 sub (+ (nth prev-v0 (dec i)) cost)]
                             (conj v (min del ins sub))))
                         [(inc (first prev-v0))]
                         (range 1 (inc (count s2))))))))))

(defn judge-answer [user-input solution]
  (let [dist (levenshtein (if (nil? user-input) "" (str/trim user-input))
                          solution)
        len (count solution)]
    (cond
      (= dist 0) :perfect
      (<= dist (if (> len 5) 2 1)) :close
      :else :wrong)))

(defn mask-solution [solution revealed-set]
  (apply str
         (map (fn [c]
                (if (or (= c \space) (contains? revealed-set c))
                  c
                  "_"))
              solution)))

(defn get-random-hint-letters [solution revealed-set]
  (let [candidates (remove #(or (= % \space) (contains? revealed-set %)) solution)
        distinct-candidates (distinct candidates)]
    (take 2 (shuffle distinct-candidates))))

(defn seeded-shuffle [coll seed-str]

  (let [al (java.util.ArrayList. coll)
        rng (if (str/blank? seed-str)
              (Random.)
              (Random. (.hashCode seed-str)))]
    (Collections/shuffle al rng)
    (vec al)))

(defn time-up? [state]
  (let [limit (:time-limit state)
        start (:start-time state)
        now (System/currentTimeMillis)]
    (> (- now start) limit)))

(defn get-remaining-time [state]
  (let [limit (:time-limit state)
        start (:start-time state)
        now (System/currentTimeMillis)
        remaining (- limit (- now start))]
    (max 0 (int (/ remaining 1000)))))