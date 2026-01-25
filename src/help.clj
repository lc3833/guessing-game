(ns help
  (:require [achievements]
            [ui]))

(defn print-rules []
  (println "\n==========================================")
  (println "          📜 GAME RULES & HELP 📜")
  (println "==========================================")
  (println "1. GOAL: Guess the word based on ASCII art.")
  (println "2. MODES: Easy, Medium, Hard, Hardcore.")
  (println "3. SCORING: Faster answers + Streaks = More Points!")
  (println "\n--- ⌨️ COMMANDS ---")
  (println "  /hint       - Reveal 2 random letters (Costs hints!)")
  (println "  /sound on   - Turn sound ON")
  (println "  /sound off  - Turn sound OFF")
  (println "  /sound 50   - Set volume to 50%")
  (println "  /help       - Show this menu")
  (println "  exit        - Quit the game")

  (println "\n--- 🏆 ACHIEVEMENTS GUIDE ---")
  (doseq [[key info] (sort-by #(get-in % [1 :name]) achievements/criteria)]
    (println (format "  %-20s : %s" (:name info) (:desc info))))

  (println "==========================================\n"))