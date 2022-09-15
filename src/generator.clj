(ns generator
  (:require [clojure.edn :as edn]
            [com.rpl.specter :refer :all]))

;;;helper predicate to find out whether
;;; elm in containrd in the collection coll
(defn in? [elm coll]
  (some #(= % elm) coll))

;;;"vectorize" a scalar point
(defn gen-vector-of[unit repetitions]
  (reduce (fn[v _] (conj v unit)) (vector) (range 1 (inc repetitions))))


;;;generate schedule based on data
;;;provided in curriculum
;;;result is a string in .edn format
(defn generate-schedule [curriculum]
  ;;;TBD
  (str (curriculum :subjs)))


;;;find professors able to teach given subject and add them to subject
(defn enrich-subjs-with-prof-ids [curriculum]
  ;;;enrich each subjects with a list of
  ;;;professors able to teach it
  (transform [:subjs ALL]
             (fn [sbj]
               (assoc sbj
                      :profs
                      (select [:profs ALL #(in? (sbj :id) (% :subjs))] curriculum)))
             curriculum))


(defn enrich-subjs-with-group-ids [curriculum]
  ;;;enrich each subjects with a list of
  ;;;groups slated to learn it
  (transform [:subjs ALL]
             (fn [sbj]
               (assoc sbj
                      :groups
                      (select [:groups ALL #(in? (sbj :id) (% :subjs))] curriculum)))
             curriculum))


(defn calculate-number-of-lessons [curriculum]
  (let [numlessons (reduce #(+ %1 %2) 0 (select [:subjs ALL :hours] curriculum))
        days (count (select [:days ALL] curriculum))
        lessonsPerDay (count (select [:time-slots ALL] curriculum))]
    {:total-number-of-lessons numlessons
     :number-of-weeks (Math/floor (/  numlessons (* days lessonsPerDay)))
     :remainder-lessons (mod numlessons (* days lessonsPerDay))}))



;;;generate days x times slots of a week
;;;based on curriculum's
;;;time slots and days configuration
(defn gen-week-timeslots [curriculum]
  (let [res (zipmap (select [:days ALL] curriculum)
                    (select [:days ALL] curriculum))]
    (transform [ALL LAST]
               (fn [_] (first (select [:time-slots] curriculum)))
               res)))

;;;calculate required number of days
;;;and generate schedule
(defn gen-time-table [curriculum]
  (let [{t :total-number-of-lessons
         w :number-of-weeks
         r :remainder-lessons} (calculate-number-of-lessons curriculum)
        res {:schedule []}]
    (setval :schedule 
      (gen-vector-of 
        (gen-week-timeslots curriculum) w) res )))



;;;some tests
(def data (edn/read-string (slurp "data/ex1.edn")))
(enrich-subjs-with-prof-ids data)
(enrich-subjs-with-group-ids data)
(gen-week-timeslots data)
(calculate-number-of-lessons data)
(gen-time-table data)
(gen-vector-of (gen-time-table data) 2)
