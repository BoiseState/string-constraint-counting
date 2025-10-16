(declare-const y String)
(declare-const y1 String)
(declare-const y2 String)
(declare-const y3 String)
(declare-const x String)
(declare-const z String)

(assert (= y1 (str.replace_all y "u" "A")))
(assert (= y2 (str.replace_all y1 "a" "T")))
(assert (= y3 (str.replace_all y2 "g" "C")))
(assert (= x  (str.replace_all y3 "c" "G")))

(assert (= x "TGCTGACATCATCTGTACCATGGAGTGTGTACCGTCTGAGACCATTACAGTTTATCTGAAGTATAAAAGGTCTCCATGCAGTTGCGGCGAAACTCACTATGTTAGTGATTAATCTCTCTGCGCACGCATGACTCAATGAAAAAGTGAATCGTATCACTGGCAGCACATGAACCCTGATTCGGATAAGTAAAGGACGATAC"))
(assert (= z "gccgugaccgcuuaa"))

(assert (str.contains y z))

(check-sat)
(exit)