(ns generator
  (:require [clojure.edn :as edn]
            [com.rpl.specter :refer :all]))


(defn in? [elm coll]
  (some #(= % elm) coll))

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
  (transform [:subjs ALL ]
    (fn [sbj]
      (assoc sbj 
             :profs
             (select [:profs ALL #(in? (sbj :id) (% :subjs))] curriculum))) 
        curriculum))


;;;some tests
(def data (edn/read-string (slurp "data/ex1.edn")))
(enrich-subjs-with-prof-ids data)
