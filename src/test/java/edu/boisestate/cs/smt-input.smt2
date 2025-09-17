(declare-const s1_1 String)
(declare-const s2_2 String)

(assert (str.suffixof s1_1 s2_2))
(assert (not (str.suffixof s2_2 s1_1)))

(check-sat)
(exit)