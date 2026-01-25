(ns achievements
  (:import [java.time LocalTime]))


(def criteria
  {
   :completionist   {:name "🏆 THE COMPLETIONIST" :desc "Solved EVERY puzzle in the database!"}


   :nature-lover    {:name "🌿 Nature Lover" :desc "Solved 5 puzzles in Nature category."}
   :tech-wizard     {:name "💻 Tech Wizard"  :desc "Solved 5 puzzles in Objects category."}
   :globetrotter    {:name "🌍 Globetrotter" :desc "Solved 5 puzzles in Places category."}


   :speed-demon     {:name "⚡ Speed Demon" :desc "Guessed in under 5 seconds!"}
   :sonic           {:name "🦔 Sonic"       :desc "Guessed in under 2 seconds!"}
   :quick-thinker   {:name "🧠 Quick Thinker" :desc "3 consecutive fast guesses (<10s)."}


   :streak-master   {:name "🔥 Streak Master" :desc "Reached a 5x streak!"}
   :double-digits   {:name "🔥🔥 Double Digits" :desc "Reached a 10x streak!"}
   :unstoppable     {:name "🚀 Unstoppable"   :desc "Reached a 20x streak!"}


   :big-winner      {:name "💰 Big Winner" :desc "Scored over 2000 points in a single round!"}
   :high-roller     {:name "💎 High Roller" :desc "Scored over 3000 points in a single round!"}
   :centurion       {:name "💯 Centurion"   :desc "Total lifetime score over 100,000!"}


   :hardcore-hero   {:name "💀 Hardcore Hero" :desc "Won a round on Hardcore mode!"}
   :invincible      {:name "🛡️ Invincible"    :desc "Won a Hardcore round with full lives!"}


   :eagle-eye       {:name "🦅 Eagle Eye" :desc "Guessed correctly without any hints!"}
   :hint-hater      {:name "🚫 Hint Hater" :desc "Solved 10 puzzles total without ever using a hint!"}
   :sharpshooter    {:name "🎯 Sharpshooter" :desc "10 Perfect answers in a row!"}


   :regular         {:name "👋 Regular" :desc "Played 5 separate sessions."}
   :night-owl       {:name "🦉 Night Owl" :desc "Played a game between 00:00 and 05:00."}
   :early-bird      {:name "🌅 Early Bird" :desc "Played a game between 05:00 and 08:00."}
   :cyber-sentry    {:name "🤖 Cyber Sentry" :desc "Solved 10 puzzles total."}})

(defn get-current-hour []
  (.getHour (LocalTime/now)))

(defn evaluate [game-state round-data player-stats total-db-count]
  (let [{:keys [result time-left total-time hints-used points]} round-data
        {:keys [streak difficulty-mode lives]} game-state


        total-solved-count (count (:solved-ids player-stats))
        category-counts (:category-counts player-stats)
        total-score (:total-score player-stats)
        no-hint-count (:no-hint-count player-stats)
        sessions (:sessions player-stats)
        hour (get-current-hour)]

    (cond-> #{}
            (= total-solved-count total-db-count) (conj :completionist)


            (>= (get category-counts :nature 0) 5)  (conj :nature-lover)
            (>= (get category-counts :objects 0) 5) (conj :tech-wizard)
            (>= (get category-counts :places 0) 5)  (conj :globetrotter)

            (and (= result :perfect) (< (- total-time time-left) 5)) (conj :speed-demon)
            (and (= result :perfect) (< (- total-time time-left) 2)) (conj :sonic)

            (>= streak 5)  (conj :streak-master)
            (>= streak 10) (conj :double-digits)
            (>= streak 20) (conj :unstoppable)

            (>= points 2000) (conj :big-winner)
            (>= points 3000) (conj :high-roller)
            (>= total-score 100000) (conj :centurion)

            (and (= result :perfect) (= difficulty-mode :hardcore)) (conj :hardcore-hero)
            (and (= result :perfect) (= difficulty-mode :hardcore) (= lives 3)) (conj :invincible)

            (and (= result :perfect) (zero? hints-used)) (conj :eagle-eye)
            (>= no-hint-count 10) (conj :hint-hater)

            (and (>= hour 0) (< hour 5)) (conj :night-owl)
            (and (>= hour 5) (< hour 8)) (conj :early-bird)
            (>= sessions 5) (conj :regular)
            (>= total-solved-count 10) (conj :cyber-sentry))))