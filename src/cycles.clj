(ns cycles)

;;; zeroes-and-ones-cycle cycle
;;; is a sequence of zeroes and ones
;;; generate all N cycles
(defn gen-cycles[zeroes-and-ones-cycle]
  (loop [res [zeroes-and-ones-cycle] i (count zeroes-and-ones-cycle)]
    (if (= i 1)
      res
        (recur
         (conj
          res
          (into [] (conj (butlast (last res))
                         (last (last res))))) (dec i)))))

(defn take-last-n-of-each [n matrix]
  (mapv #(into [] (take-last n %)) matrix))


;;n = 1
(->> (gen-cycles [0 1])
     (take-last-n-of-each 1))
 
;;n = 2
(->>(gen-cycles [0 0 1 1])
    (take-last-n-of-each 2))

;;n =3
(->>(gen-cycles [0 0 0 1 0 1 1 1])
    (take-last-n-of-each 3))






