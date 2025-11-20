(declare-const string0 String)

(assert (not (str.suffixof "hello" string0)))
(assert (= (str.indexof string0 "WWWWW's Birthday is 12-17-77") 2))