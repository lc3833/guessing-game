(ns state)

(def game-state (atom {:nickname "Guest"
                       :score 0
                       :streak 0
                       :lives 3
                       :database []
                       :current-mode :mix
                       :puzzle-queue []
                       :difficulty-mode :medium
                       :time-limit 0
                       :start-time 0
                       :hints 0}))

(defn reset-game-stats! []
  (swap! game-state merge {:score 0
                           :streak 0
                           :lives 3
                           :puzzle-queue []
                           :hints 0}))