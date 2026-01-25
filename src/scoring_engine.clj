(ns scoring-engine)

(defn calculate-points [result time-left total-time current-streak]
  (let [
        base-value (if (= result :perfect) 1000 500)
        time-bonus (if (= result :perfect)
                     (int (* 1000 (/ time-left total-time)))
                     0)
        multiplier (+ 1 (/ current-streak 10))]
    (int (* (+ base-value time-bonus) multiplier))))

(defn update-streak [current-streak result]
  (if (= result :perfect)
    (inc current-streak)
    0))