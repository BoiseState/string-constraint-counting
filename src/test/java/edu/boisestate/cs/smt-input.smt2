(declare-const string0 String)
(declare-const string1 String)
(declare-const string2 String)

(assert (not (= "A" string0)))
(assert (not (= "" string1)))
(assert (= (str.++ string1 string2) string0))

(check-sat)
(exit)