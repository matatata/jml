# JML

An experimental Parser and Java-Interpreter for a functional language inspired by SML

This is a toy project that I've created to learn more about functional programming languages and particularly [Standard ML](https://en.wikipedia.org/wiki/Standard_ML). Although this interpreter lacks some key features of SML like compile-time type checking and type inference, I've implemented much of the language's core features and added means to interact with Java-Objects.

I'm not actively working on this project and had never had any serious plans with it. If you're interested in functional programming on the JVM take a look at [Scala](https://www.scala-lang.org) that has been influenced by SML.

Build from source:

`mvn clean install`

Run the interactive read-eval-print-loop:

`java -jar target/jml-*-jar-with-dependencies.jar`


Now type in expressions and declarations separated by ";" and terminated by ";;"


Define the function "factorial"

```
fun factorial 0 = 1
	| factorial n = n * factorial (n-1)
;;
```

and then call it `factorial 10 ;;`

The language's grammar is defined in [FL.g](src/main/antlr3/fl/frontend/parser/FL.g).

See [test.fl](src/test/resources/test.fl) and [global.fl](src/main/resources/global.fl) to get a glipse of the supported syntax and functionality.


