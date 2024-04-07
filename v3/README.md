Rough POC for a custom Kotest JS Browser tests runner.

Run `gradle :v3:jsTestKotest`, and some of the tests will be shown in IntelliJ.
(I'm not sure why they don't all render, I guess something isn't quite right with the IJLog XML, but it's fixable.)

How it works:

1. Tests are defined in src/jsTest using a custom test DSL (suspended nesting is supported)
2. KGP compiles src/jsTest/kotlin to .js
3. a custom Gradle Test task uses a custom JUnit TestEngine
4. The custom JUnit TestEngine hosts uses Ktor to host the .js files, and an index.html
5. Playwright visits the hosted index.html
6. The tests run (which is magic to me, I guess they run automatically because they're in a main function?)
7. The custom test DSL logs test/suite started/finished events (to the browser console)
8. Playwright has a console listener, which parses the event, converts them to IJLog XML, and prints them to stdout.
9. IntelliJ picks up the IJLog XML, and renders the tests in IntelliJ.
