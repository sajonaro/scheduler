(ns scheduler
;;(:require [clojure.core.matrix :refer :all :exclude [abs]])
  (:gen-class))
(:use 'clojure.core)

(:use 'clojure.core.matrix)

(defn -main [& args]
  "entry point"
  (println (slurp (first args))))