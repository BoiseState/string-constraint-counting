#! /bin/bash

# run mvn install to compile and build the latest version of MAS
mvn install

# copy the lates version of the MAS jar to the SPF lib directory
cp target/string-constraint-solvers-*-dependencies.jar ../SPF/jpf-symbc/lib/MAS.jar