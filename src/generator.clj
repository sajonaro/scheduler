(ns generator
  (:require [clojure.edn :as edn]
            [clojure.math.combinatorics :refer :all]
            [com.rpl.specter :refer :all]))

;;;helper predicate to find out whether
;;;elm in containrd in the collection coll
(defn in? [elm coll]
  (some #(= % elm) coll))

;;;for two sequences and given number
;;;generate  composition 
;;; e.g. colA ={ a b c } colB={x y} n = 5
;;; NB n < |colA| * |colB|
;;; expected output {:a [ x y] b: [x y] c:[x]}
(defn arrange-first-against-second[collA collB numtotal]
 (let [ cp (take numtotal (cartesian-product collA collB))]
   (transform [MAP-VALS]
              (fn[v] (mapv #(last %) v))
              (group-by (fn [e] (first e)) cp))))


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
(defn gen-complete-week-timeslots [curriculum]
  (let [res (zipmap (select [:days ALL] curriculum)
                    (select [:days ALL] curriculum))]
    (transform [ALL LAST]
               (fn [_] (first (select [:time-slots] curriculum)))
               res)))

;;;calculate required number of days
;;;and generate schedule
(defn gen-time-table [curriculum]
  (let [{w :number-of-weeks
         r :remainder-lessons} (calculate-number-of-lessons curriculum)
        res {:schedule []}]
    (setval :schedule
      (conj 
        (gen-vector-of
          (gen-complete-week-timeslots curriculum) w)
        (->>(arrange-first-against-second
             (first (select [:days] curriculum))
             (first (select [:time-slots] curriculum))
             r)
             (transform [MAP-VALS] #(into {} %))))
       res )))

;;;some tests
(def data (edn/read-string (slurp "data/ex1.edn")))
(enrich-subjs-with-prof-ids data)
(enrich-subjs-with-group-ids data)
(gen-complete-week-timeslots data)
(calculate-number-of-lessons data)
(gen-time-table data)
(gen-vector-of (gen-time-table data) 2)
(arrange-first-against-second
 (first (select [:days] data))
 (first (select [:time-slots] data))
 7)

(defn convert-map-vals-to-maps[mvctr]
  (transform [MAP-VALS] 
    (fn[v]
      (into {} v))
    mvctr))

(convert-map-vals-to-maps 
 (arrange-first-against-second
  (first (select [:days] data))
  (first (select [:time-slots] data))
  7))

(->> (arrange-first-against-second
      (first (select [:days] data))
      (first (select [:time-slots] data))
      7)
     (transform [MAP-VALS] #(into {} %)))

(def tst [[:a [8.45 9.3]]
           [:b [9.45 10.3]]
           [:c [10.45 11.3]]])

(->> tst
     ;;(map #([(first %) (second %)]))
     (into {}))

(into {} tst)