plugins {
    `java-library`
}


val testOutputLoggingIJ by tasks.registering(Test::class) {
    group = project.name

    // need to have something as an input, otherwise Gradle skips the tasks
    testClassesDirs = files(".")

    doLast {
        //@formatter:off
        println("""<ijLog><event type='beforeSuite'><test id='jsNodeTest' parentId=''><descriptor name='jsNodeTest' displayName='jsNodeTest' className='' /></test></event></ijLog>""")
        println("""<ijLog><event type='beforeSuite'><test id='jsNodeTest//TestJs' parentId='jsNodeTest'><descriptor name='jsNodeTest.TestJs' displayName='TestJs' className='TestJs' /></test></event></ijLog>""")
        println("""<ijLog><event type='beforeTest'><test id='jsNodeTest//TestJs/kotlinTestTests' parentId='jsNodeTest//TestJs'><descriptor name='kotlinTestTests' displayName='kotlinTestTests[js, node]' className='TestJs' /></test></event></ijLog>""")
        println("""<ijLog><event type='afterTest'><test id='jsNodeTest//TestJs/kotlinTestTests' parentId='jsNodeTest//TestJs'><descriptor name='kotlinTestTests' displayName='kotlinTestTests[js, node]' className='TestJs' /><result resultType='SUCCESS' startTime='1712049799267' endTime='1712049799270'><failureType>error</failureType></result></test></event></ijLog>""")
        println("""<ijLog><event type='afterSuite'><test id='jsNodeTest//TestJs' parentId='jsNodeTest'><descriptor name='jsNodeTest.TestJs' displayName='TestJs' className='TestJs' /><result resultType='SUCCESS' startTime='1712049799267' endTime='1712049799273'><failureType>error</failureType></result></test></event></ijLog>""")
        println("""<ijLog><event type='beforeSuite'><test id='jsNodeTest/nestable-async-via-kotlin-test' parentId='jsNodeTest'><descriptor name='jsNodeTest.nestable-async-via-kotlin-test' displayName='nestable-async-via-kotlin-test' className='nestable-async-via-kotlin-test' /></test></event></ijLog>""")
        println("""<ijLog><event type='beforeTest'><test id='jsNodeTest/nestable-async-via-kotlin-test/should-fail' parentId='jsNodeTest/nestable-async-via-kotlin-test'><descriptor name='should-fail' displayName='should-fail[js, node]' className='nestable-async-via-kotlin-test' /></test></event></ijLog>""")
        println("""<ijLog><event type='afterTest'><test id='jsNodeTest/nestable-async-via-kotlin-test/should-fail' parentId='jsNodeTest/nestable-async-via-kotlin-test'><descriptor name='should-fail' displayName='should-fail[js, node]' className='nestable-async-via-kotlin-test' /><result resultType='FAILURE' startTime='1712049799273' endTime='1712049799274'><errorMsg><![CDATA[QXNzZXJ0aW9uRXJyb3I6IHRoaXMtaXMtYS1mYWlsdXJl]]></errorMsg><stackTrace><![CDATA[QXNzZXJ0aW9uRXJyb3I6IHRoaXMtaXMtYS1mYWlsdXJlCglhdCA8Z2xvYmFsPi5UZXN0SnMka290bGluVGVzdFRlc3RzJGxhbWJkYSRsYW1iZGEoL1VzZXJzL2Rldi9wcm9qZWN0cy9leHRlcm5hbC9rb3RsaW4tanMtd2FzbS10ZXN0aW5nL3dpdGgta290bGluLXRlc3Qvc3JjL2pzVGVzdC9rb3RsaW4va290bGluVGVzdC5rdDo0NSkKCWF0IENvbnRleHQuPGFub255bW91cz4oL1VzZXJzL2Rldi9wcm9qZWN0cy9leHRlcm5hbC9rb3RsaW4tanMtd2FzbS10ZXN0aW5nL3dpdGgta290bGluLXRlc3Qvc3JjL2pzVGVzdC9rb3RsaW4va290bGluVGVzdC5rdDo2MSkKCWF0IDxnbG9iYWw+LnByb2Nlc3NJbW1lZGlhdGUobm9kZTppbnRlcm5hbC90aW1lcnM6NDc4KQo=]]></stackTrace><failureType>error</failureType></result></test></event></ijLog>""")
        println("""<ijLog><event type='beforeSuite'><test id='jsNodeTest/nestable-async-via-kotlin-test/container' parentId='jsNodeTest'><descriptor name='jsNodeTest.nestable-async-via-kotlin-test.container' displayName='nestable-async-via-kotlin-test.container' className='nestable-async-via-kotlin-test.container' /></test></event></ijLog>""")
        println("""<ijLog><event type='beforeTest'><test id='jsNodeTest/nestable-async-via-kotlin-test/container/should-pass' parentId='jsNodeTest/nestable-async-via-kotlin-test/container'><descriptor name='should-pass' displayName='should-pass[js, node]' className='nestable-async-via-kotlin-test.container' /></test></event></ijLog>""")
        println("""<ijLog><event type='afterTest'><test id='jsNodeTest/nestable-async-via-kotlin-test/container/should-pass' parentId='jsNodeTest/nestable-async-via-kotlin-test/container'><descriptor name='should-pass' displayName='should-pass[js, node]' className='nestable-async-via-kotlin-test.container' /><result resultType='SUCCESS' startTime='1712049799278' endTime='1712049799278'><failureType>error</failureType></result></test></event></ijLog>""")
        println("""<ijLog><event type='afterSuite'><test id='jsNodeTest/nestable-async-via-kotlin-test/container' parentId='jsNodeTest'><descriptor name='jsNodeTest.nestable-async-via-kotlin-test.container' displayName='nestable-async-via-kotlin-test.container' className='nestable-async-via-kotlin-test.container' /><result resultType='SUCCESS' startTime='1712049799278' endTime='1712049799279'><failureType>error</failureType></result></test></event></ijLog>""")
        println("""<ijLog><event type='afterSuite'><test id='jsNodeTest/nestable-async-via-kotlin-test' parentId='jsNodeTest'><descriptor name='jsNodeTest.nestable-async-via-kotlin-test' displayName='nestable-async-via-kotlin-test' className='nestable-async-via-kotlin-test' /><result resultType='FAILURE' startTime='1712049799273' endTime='1712049799279'><failureType>error</failureType></result></test></event></ijLog>""")
        println("""<ijLog><event type='afterSuite'><test id='jsNodeTest' parentId=''><descriptor name='jsNodeTest' displayName='jsNodeTest' className='' /><result resultType='FAILURE' startTime='1712049799161' endTime='1712049799280'><failureType>error</failureType></result></test></event></ijLog>""")
        //@formatter:on
    }
}


val testOutputLoggingTCSM by tasks.registering(Test::class) {
    group = project.name

    testClassesDirs = files(".")

    doLast {
        //@formatter:off
        println("""##teamcity[testSuiteStarted name=' TestJs.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testStarted name='kotlinTestTests' captureStandardOutput='true' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testFinished name='kotlinTestTests' duration='' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testSuiteFinished name=' TestJs.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testSuiteStarted name='nestable-async-via-kotlin-test.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testStarted name='should-fail' captureStandardOutput='true' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testFailed name='should-fail' message='FAILED' details='AssertionError: this-is-a-failure|n    at TestJs_kotlinTestTests_lambda_lambda_0 (/Users/dev/projects/external/kotlin-js-wasm-testing/with-kotlin-test/src/jsTest/kotlin/kotlinTest.kt:45:23 <- kotlin-js-wasm-testing-with-kotlin-test-test.1338310944.js:59:11)|n    at Context.<anonymous> (/Users/dev/projects/external/kotlin-js-wasm-testing/with-kotlin-test/src/jsTest/kotlin/kotlinTest.kt:61:9 <- kotlin-js-wasm-testing-with-kotlin-test-test.1338310944.js:103:7)|n' captureStandardOutput='true' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testFinished name='should-fail' duration='' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testSuiteFinished name='nestable-async-via-kotlin-test.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testSuiteStarted name='nestable-async-via-kotlin-test container.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testStarted name='should-pass' captureStandardOutput='true' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testFinished name='should-pass' duration='' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[testSuiteFinished name='nestable-async-via-kotlin-test container.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC133633098619180566']""")
        println("""##teamcity[blockClosed name='JavaScript Unit Tests' flowId='%s']""")
        println("""##teamcity[blockOpened name='JavaScript Unit Tests' flowId='%s']""")
        println("""##teamcity[message text='02 04 2024 11:08:36.559:INFO |[karma-server|]: Karma v6.4.2 server started at http://localhost:9876/' type='INFO']""")
        println("""##teamcity[message text='02 04 2024 11:08:36.560:INFO |[launcher|]: Launching browsers ChromeHeadless with concurrency unlimited' type='INFO']""")
        println("""##teamcity[message text='02 04 2024 11:08:36.562:INFO |[launcher|]: Starting browser ChromeHeadless' type='INFO']""")
        println("""##teamcity[message text='02 04 2024 11:08:37.385:INFO |[Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)|]: Connected on socket AZTFgf7N3OvZCk6RAAAB with id 19180566' type='INFO']""")
        //@formatter:on
    }
}
