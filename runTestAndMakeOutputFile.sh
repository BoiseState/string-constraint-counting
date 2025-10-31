#!/bin/bash

method=$1
mvn -q -Dtest=edu.boisestate.cs.SolveMainTest#"$method" test | grep -vE '^\s*(-+|T E S T S|Running.*|Tests.*|Results.*|\s*$)' > src/test/resources/out/"$method".txt