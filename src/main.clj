(ns main
  (:gen-class)
  (:require [generator :as gen]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn -main
  ([]
   (println "please provide path to curriculum data file: *.edn"))
  ([path]
   ;;;by default, generate output into result_schedule.edn file
   (with-open [w (io/writer "result_schedule.edn" :append false)]
     (.write w (gen/generate-schedule (edn/read-string (slurp path)))))))

