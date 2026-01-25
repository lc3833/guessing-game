(ns sound
  (:require [clojure.string :as str])
  (:import [javax.sound.sampled AudioSystem FloatControl$Type Clip LineEvent LineEvent$Type LineListener]))

(def global-volume (atom 50))

(defn- get-db [percent]
  (let [safe-percent (max 0.0001 (min 100.0 percent))]
    (if (zero? percent) -80.0 (* 20.0 (Math/log10 (/ safe-percent 100.0))))))

(defn play [filename]
  (future
    (try
      (let [url (clojure.java.io/resource filename)]
        (if url
          (let [audio-in (AudioSystem/getAudioInputStream url)
                clip (AudioSystem/getClip)]
            (.addLineListener clip (reify LineListener (update [_ event] (when (= (.getType event) LineEvent$Type/STOP) (.close clip)))))
            (.open clip audio-in)
            (try
              (let [gain-control (.getControl clip FloatControl$Type/MASTER_GAIN)
                    db (get-db @global-volume)]
                (.setValue gain-control (float db)))
              (catch Exception _))
            (.start clip))
          (println "⚠️ ERROR: Sound not found:" filename)))
      (catch Exception e (println "⚠️ SOUND ERROR:" (.getMessage e))))))

(defn set-volume [percent]
  (reset! global-volume percent))

(defn handle-command [cmd-string]
  (let [cmd (second (str/split cmd-string #"\s+"))]
    (cond
      (or (= cmd "on") (= cmd "start"))
      (do (set-volume 50) "🔊 Sound ON (50%)")

      (or (= cmd "off") (= cmd "stop"))
      (do (set-volume 0) "🔇 Sound OFF (0%)")

      (re-matches #"\d+" (str cmd))
      (let [vol (Integer/parseInt cmd)]
        (set-volume vol)
        (str "🔊 Volume set to " vol "%"))
      :else
      "Usage: /sound [on|off|0-100]")))