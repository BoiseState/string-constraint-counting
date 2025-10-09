(declare-const string0 String)

(assert (not (= (str.substr string0 20) "erationatdifffblue")))
(assert (>= (str.len string0) 20))

(check-sat)
(exit)