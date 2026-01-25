(ns core
  (:require [clojure.string :as str]
            [db]
            [game-flow]
            [state]
            [ui]
            [sound]
            [help])
  (:gen-class))

(defn main-menu-loop []
  (loop []
    (println (str "\nWelcome, " (:nickname @state/game-state) "!"))
    (println "==========================================")
    (println "           🎮 MAIN MENU 🎮")
    (println "==========================================")
    (println "1. 🕹️ PLAY NEW GAME")
    (println "2. 📜 HELP & RULES")
    (println "3. 🔊 SOUND SETTINGS")
    (println "4. 🏆 HIGH SCORES")
    (println "5. 🏅 ACHIEVEMENTS")
    (println "6. 🚪 EXIT")
    (println "==========================================")
    (println "Credits to: Lazar Cvetković 2025/3833")
    (println "==========================================")
    (print "Choose option (1-6): ") (flush)

    (let [choice (str/trim (read-line))]
      (case choice
        "1" (do (game-flow/start-game-session) (recur))

        "2" (do (help/print-rules)
                (println "\nPress ENTER to return...")
                (read-line)
                (recur))

        "3" (do (println "\nEnter /volume (0-100) or 'on'/'off':")
                (print "> ") (flush)
                (println (sound/handle-command (read-line)))
                (recur))

        "4" (do
              (let [scores (db/load-high-scores)]
                (ui/print-full-leaderboard scores))
              (println "\nPress ENTER to return...")
              (read-line)
              (recur))

        "5" (do
              (println "\nEnter nickname to view achievements:")
              (print "> ") (flush)
              (let [target-nick (str/trim (read-line))]
                (if (str/blank? target-nick)
                  (println "Invalid nickname.")
                  (println (db/get-achievements-for-player target-nick))))
              (println "\nPress ENTER to return...")
              (read-line)
              (recur))

        "6" (do
              (println "\nThanks for playing! Bye! 👋")
              (println "\n[Press ENTER to exit]")
              (read-line)
              (System/exit 0))


        (do (println "Invalid option. Please type 1, 2, 3, 4, 5 or 6.")
            (recur))))))

(defn -main []
  (ui/print-welcome)
  (let [loaded-data (db/load-database "drawings.edn")]
    (if (empty? loaded-data)
      (println "Game cannot start without database.")
      (do
        (swap! state/game-state assoc :database loaded-data)
        (print "Enter your nickname: ") (flush)
        (let [nick (read-line)
              final-nick (if (str/blank? nick) "Guest" nick)]
          (swap! state/game-state assoc :nickname final-nick)
          (db/increment-session final-nick))
        (main-menu-loop)))))