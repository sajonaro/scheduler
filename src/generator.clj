(ns generator)


;;;generate schedule based on data
;;;provided in curriculum
;;;result is a string in .edn format
(defn generate-schedule [curriculum]
  ;;;TBD
  (str (curriculum :subjs)))