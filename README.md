### Experimental Kotlin/JS and Kotlin/Wasm/JS test infrastructure

This project aims to experiment with test runners on the JS platform, in particular regarding

* supporting asynchronous tests,
* supporting nested tests.

#### Flat Test Framework

The project features alternative flat test frameworks, selectable by setting `flatTestFramework`
in [v1/src/jsHostedMain/kotlin/Main.kt](v1/src/jsHostedMain/kotlin/Main.kt):

* `kotlinJsTestFramework`: A bare-bones implementation of the Kotlin-provided test framework used with `kotlin-test`.
* `standaloneJsFlatTestFramework`: A standalone flat test runner. It does not work with browser-based tests due to
  missing Karma integration.

Synchronous and asynchronous test invocations are supported.

#### Nested Tests Framework

Running `runNestableTests()` in [v1/src/jsHostedMain/kotlin/Main.kt](v1/src/jsHostedMain/kotlin/Main.kt)
invokes a suite with nested tests using a separate framework on top of the selected flat test framework. This produces
reasonable output when run as a normal application (`gradlew jsNodeDevelopmentRun`).

When run via test tasks with `standaloneJsFlatTestFramework`, IntelliJ IDEA does not show the correct nesting.

With `kotlinJsTestFramework`, the Kotlin test infra (if used) complains:

* ``previous test `jsNodeTest/nestable async (via JS/Mocha/transformed)/nestable async (via JS/Mocha/transformed)` not finished``
* ```Bad TCSM: unexpected node to close `should pass`, expected `nestable async (via JS/Mocha/transformed)```

#### Test Tasks

* `gradlew --continue :v1:cleanAllTests :v1:jsBrowserTest :v1:jsNodeTest :v1:wasmJsBrowserTest :v1:wasmJsNodeTest`

* `gradlew :v1:cleanJsBrowserTest :v1:jsBrowserTest`

* `gradlew :v1:cleanWasmJsBrowserTest :v1:wasmJsBrowserTest`

* `gradlew :v1:cleanJsNodeTest :v1:jsNodeTest`

* `gradlew :v1:cleanWasmJsNodeTest :v1:wasmJsNodeTest`

#### Checking unmodified test reports

Available with the `standaloneJsTestFramework` only.

* `gradlew :v1:jsNodeDevelopmentRun`

#### Background

* TeamCity
  Messages: [Service Messages \| TeamCity On\-Premises Documentation](https://www.jetbrains.com/help/teamcity/service-messages.html#Nested+Test+Reporting)
