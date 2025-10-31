(declare-const s1_10 String)
(declare-const s2_11 String)

(assert (not (str.suffixof s1_10 s2_11)))
(assert (not (str.suffixof s2_11 s1_10)))