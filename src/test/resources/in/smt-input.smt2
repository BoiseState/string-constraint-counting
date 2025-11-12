(declare-const string1 String)
(declare-const string0 String)

(assert (not (str.prefixof string1 string0)))
(assert (not (str.suffixof "c" string1)))

(check-sat)