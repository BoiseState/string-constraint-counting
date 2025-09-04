(declare-fun s () String)
(assert (not (str.contains "HelloWorld" s)))
(check-sat)
(get-model)