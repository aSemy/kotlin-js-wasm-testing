This project demonstrates how to tell IJ to render test results.

Run `gradle testOutputLoggingIJ`, and IJ will render the test results.
(Even though no tests ran, and the task only logs to stdout).

IJ ignores TCSM - run `gradle testOutputLoggingTCSM` and IJ will not render any results.
