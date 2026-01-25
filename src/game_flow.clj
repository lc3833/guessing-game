(ns game-flow
  (:require [clojure.string :as str]
            [state :refer [game-state reset-game-stats!]]
            [db]
            [game-logic]
            [scoring-engine]
            [achievements]
            [ui]
            [sound]
            [help]))

(defn handle-special-commands [input solution revealed-letters hints-used-round]
  (cond
    (= input "/help")
    (do (help/print-rules)
        (println "Press ENTER to continue...")
        (read-line)
        :recur)

    (str/starts-with? input "/sound")
    (do (println (sound/handle-command input))
        :recur)

    (or (= input "/hint") (= (str/lower-case input) "hint"))
    (if (> (:hints @game-state) 0)
      (let [new-letters (game-logic/get-random-hint-letters solution revealed-letters)]
        (if (empty? new-letters)
          (do (println "\n⚠️ All letters are already revealed!")
              :recur)
          (do
            (swap! game-state update :hints dec)
            (println (str "\n✨ HINT USED! Revealed: " (str/join ", " new-letters)))
            {:action :hint-used :new-letters new-letters})))
      (do
        (println "\n❌ NO HINTS LEFT!")
        :recur))

    :else :continue-guess))

(defn play-round []
  (if (game-logic/time-up? @game-state)
    (do (sound/play "smb_warning.wav") :timeout)

    (let [queue (:puzzle-queue @game-state)]
      (if (empty? queue)
        :won

        (let [drawing (first queue)
              solution (:solution drawing)
              total-time-sec (int (/ (:time-limit @game-state) 1000))]
          (swap! game-state assoc :puzzle-queue (rest queue))
          (loop [revealed-letters #{}
                 hints-used-round 0]
            (ui/render-art (:art drawing) (:category drawing))

            (when (seq revealed-letters)
              (println "\nHINT:" (game-logic/mask-solution solution revealed-letters)))

            (println (str "⏳ TIME: " (game-logic/get-remaining-time @game-state) "s | 💡 HINTS: " (:hints @game-state)
                          " | 🔥 STREAK: " (:streak @game-state) "x"
                          " | ❤️ LIVES: " (:lives @game-state)))

            (println "Type answer, '/hint', '/help' or '/sound' (or 'exit'):")
            (print "YOUR INPUT: ") (flush)

            (let [raw-input (read-line)
                  clean-input (if raw-input (str/trim raw-input) "")]

              (if (game-logic/time-up? @game-state)
                (do (sound/play "smb_warning.wav") :timeout)

                (if (= clean-input "exit")
                  :exit
                  (let [cmd-result (handle-special-commands clean-input solution revealed-letters hints-used-round)]
                    (cond
                      (= cmd-result :recur) (recur revealed-letters hints-used-round)

                      (map? cmd-result) (recur (into revealed-letters (:new-letters cmd-result))
                                               (inc hints-used-round))
                      :else
                      (let [result (game-logic/judge-answer clean-input solution)]
                        (if (or (= result :perfect) (= result :close))
                          (let [time-left (game-logic/get-remaining-time @game-state)
                                current-streak (:streak @game-state)
                                _ (if (= result :perfect) (sound/play "smb_coin.wav") (sound/play "smb_fireball.wav"))
                                points (scoring-engine/calculate-points result time-left total-time-sec current-streak)
                                new-streak (scoring-engine/update-streak current-streak result)
                                player-stats (db/update-player-stats (:nickname @game-state) (:id drawing) (:category drawing) points hints-used-round)
                                round-data {:result result :points points :time-left time-left :total-time total-time-sec :hints-used hints-used-round}
                                total-db-count (count (:database @game-state))
                                newly-unlocked (db/save-new-achievements (:nickname @game-state) (achievements/evaluate @game-state round-data player-stats total-db-count))]

                            (when (contains? newly-unlocked :completionist) (sound/play "smb_pause.wav"))
                            (swap! game-state (fn [s] (-> s (update :score + points) (assoc :streak new-streak))))

                            (println (str "\n" (if (= result :perfect) "✅ PERFECT!" "⚠️ CLOSE!")))
                            (when (= result :close) (println (str "✅ Correct answer was: " solution)))
                            (println (str "Points: +" points " | New Streak: " new-streak "x"))

                            (when (seq newly-unlocked)
                              (println "\n🏆 UNLOCKED NEW ACHIEVEMENTS:")
                              (doseq [ach newly-unlocked] (println (str "   >>> " (:name (get achievements/criteria ach)) " <<<"))))
                            :continue)

                          (do
                            (sound/play "smb_fireworks.wav")
                            (swap! game-state (fn [s] (-> s (assoc :streak 0) (update :lives dec))))
                            (println "\n❌ WRONG. Answer:" solution)
                            :continue))))))))))))))

(defn start-game-session []
  (reset-game-stats!)

  (print "Enter SEED (leave blank for random): ") (flush)
  (let [seed-input (read-line)]

    (let [diff (ui/select-difficulty)
          diff-name (:name diff)]

      (let [hints-count (case diff-name :easy 3 :medium 2 :hard 1 :hardcore 0 0)
            lives-count (case diff-name :easy 3 :medium 2 :hard 1 :hardcore 0 3)]

        (swap! game-state assoc
               :difficulty-mode diff-name
               :time-limit (:time diff)
               :hints hints-count
               :lives lives-count)

        (println "\nDifficulty set to:" (str/upper-case (name diff-name)))
        (println "Hints:" hints-count "| Lives Remaining:" lives-count)))

    (let [cat (ui/select-category)
          puzzles (db/get-puzzles-for-mode (:database @game-state) cat)
          shuffled-queue (game-logic/seeded-shuffle puzzles seed-input)]

      (swap! game-state assoc
             :current-mode cat
             :puzzle-queue shuffled-queue
             :start-time (System/currentTimeMillis))

      (println "\nSelected Mode:" (str/upper-case (name cat)))
      (println "Puzzles loaded:" (count shuffled-queue)))

    (println "\nHello" (:nickname @game-state) "! Game starting...")
    (sound/play "smb_powerup.wav")

    (loop []
      (if (and (not (neg? (:lives @game-state)))
               (not (empty? (:puzzle-queue @game-state))))

        (let [status (play-round)]
          (case status
            :exit (println "\nGame stopped by player.")
            :timeout (println "\n⏰ TIME IS UP! GAME OVER!")
            (recur)))

        (if (game-logic/time-up? @game-state)
          (do (sound/play "smb_warning.wav") (println "\n⏰ TIME IS UP! GAME OVER!"))
          (if (neg? (:lives @game-state))
            (do (sound/play "smb_gameover.wav") (println "\n💀 YOU LOST ALL LIVES! GAME OVER."))
            (println "\n🎉 CONGRATULATIONS! YOU SOLVED ALL PUZZLES!")))))

    (let [final-score (:score @game-state)
          nickname (:nickname @game-state)
          diff (:difficulty-mode @game-state)]

      (ui/print-game-over final-score)
      (println "Saving score for" (str/upper-case (name diff)) "difficulty...")
      (let [top-scores (db/save-high-score nickname final-score diff)]
        (ui/print-high-scores top-scores)
        (println "\nPress ENTER to return to Main Menu...")
        (read-line)))))