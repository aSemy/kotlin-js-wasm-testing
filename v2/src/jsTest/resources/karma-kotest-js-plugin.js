/**
 * Karma starter function factory.
 *
 * This function is invoked from the wrapper.
 * @see  adapter.wrapper
 *
 * @param  {Object}   karma        Karma runner instance.
 * @param  {Object}   [jasmineEnv] Optional Jasmine environment for testing.
 * @return {Function}              Karma starter function.
 */

/**
 * Jasmine 2.0 dispatches the following events:
 *
 *  - jasmineStarted
 *  - jasmineDone
 *  - suiteStarted
 *  - suiteDone
 *  - specStarted
 *  - specDone
 */

function createStartFn(karma) {
    // throw new Error("createStartFn");
    return function () {
        // console.log(JSON.stringify(resultsHandler))
        // throw new Error("createStartFn - resultsHandler " + JSON.stringify(resultsHandler));

        // jasmineEnv = jasmineEnv || window.jasmine.getEnv()

        karma.info({
            event: 'suiteStarted',
            result: {
                id: "context",
                description: "context",
                fullName: "full name",
                parentSuiteId: null, //The ID of the suite containing this suite, or null if this is not in another describe().
                filename: null, // The name of the file the suite was defined in.
                failedExpectations: [],
            }
        });

        console.log('karma-kotest-js is starting tests!');

        // /** a single test has finished */
        karma.result(
            {
                fullName: "full name",
                //description: specResult.description,
                id: "full-name",
                // log: [],
                skipped: false,
                disabled: false, //specResult.status === 'disabled' || specResult.status === 'excluded',
                // pending: specResult.status === 'pending',
                success: true, //specResult.failedExpectations.length === 0,
                suite: ["x"],
                // time: skipped ? 0 : new _Date().getTime() - startTimeCurrentSpec,
                // executedExpectationsCount: specResult.failedExpectations.length + specResult.passedExpectations.length,
                // passedExpectations: specResult.passedExpectations,
                // properties: specResult.properties
            }
        );
        // // /** the client completed execution of all the tests */
        // resultsHandler.complete({});
        // // /** an error happened in the client */
        // // resultsHandler.error();
        // // /** other data (e.g. number of tests or debugging messages) */
        // // resultsHandler.info();
        //
        //
        // resultsHandler.info({
        //     event: 'jasmineStarted',
        //     total: data.totalSpecsDefined,
        //     specs: getAllSpecNames(jasmineEnv.topSuite())
        // });
        //
        //
        // tc.info({
        //     event: 'suiteDone',
        //     result: result
        // });
        //

        //@formatter:off
        console.log("##teamcity[testSuiteStarted name=' TestJs.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testStarted name='kotlinTestTests' captureStandardOutput='true' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testFinished name='kotlinTestTests' duration='' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testSuiteFinished name=' TestJs.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testSuiteStarted name='nestable-async-via-kotlin-test.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testStarted name='should-fail' captureStandardOutput='true' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testFailed name='should-fail' message='FAILED' details='AssertionError: this-is-a-failure|n    at TestJs_kotlinTestTests_lambda_lambda_0 (/Users/dev/projects/external/kotlin-js-wasm-testing/with-kotlin-test/src/jsTest/kotlin/kotlinTest.kt:45:23 <- kotlin-js-wasm-testing-with-kotlin-test-test.1338310944.js:59:11)|n    at Context.<anonymous> (/Users/dev/projects/external/kotlin-js-wasm-testing/with-kotlin-test/src/jsTest/kotlin/kotlinTest.kt:61:9 <- kotlin-js-wasm-testing-with-kotlin-test-test.1338310944.js:103:7)|n' captureStandardOutput='true' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testFinished name='should-fail' duration='' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testSuiteFinished name='nestable-async-via-kotlin-test.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testSuiteStarted name='nestable-async-via-kotlin-test container.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testStarted name='should-pass' captureStandardOutput='true' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testFinished name='should-pass' duration='' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[testSuiteFinished name='nestable-async-via-kotlin-test container.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC133633098619180566']");
        console.log("##teamcity[blockClosed name='JavaScript Unit Tests' flowId='%s']");
        console.log("##teamcity[blockOpened name='JavaScript Unit Tests' flowId='%s']");
        console.log("##teamcity[message text='02 04 2024 11:08:36.559:INFO |[karma-server|]: Karma v6.4.2 server started at http://localhost:9876/' type='INFO']");
        console.log("##teamcity[message text='02 04 2024 11:08:36.560:INFO |[launcher|]: Launching browsers ChromeHeadless with concurrency unlimited' type='INFO']");
        console.log("##teamcity[message text='02 04 2024 11:08:36.562:INFO |[launcher|]: Starting browser ChromeHeadless' type='INFO']");
        console.log("##teamcity[message text='02 04 2024 11:08:37.385:INFO |[Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)|]: Connected on socket AZTFgf7N3OvZCk6RAAAB with id 19180566' type='INFO']");
        //@formatter:on
    }
}

window.__karma__.start = createStartFn(window.__karma__)
