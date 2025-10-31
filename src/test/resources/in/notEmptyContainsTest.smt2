(declare-const s1_18 String)
(declare-const s2_19 String)

(assert (str.contains s1_18 s2_19))
(assert (not (= (str.len s2_19) 0)))