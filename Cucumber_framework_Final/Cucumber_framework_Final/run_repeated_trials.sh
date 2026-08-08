#!/usr/bin/env bash
# ADDED (Aug 2026 revision): Runs the Maven test suite N times in a row so
# test-output/timing-log.csv accumulates enough repeated measurements to
# report mean/SD/95% CI per scenario, as required by the paper's Experimental
# Design section (RQ1/RQ4). Excludes the live Flipkart login scenario by
# default -- see the note in Mylogin.java about not load-testing production
# auth.
#
# Usage:
#   chmod +x run_repeated_trials.sh
#   ./run_repeated_trials.sh 30
#
# Requires: Java 8+, Maven, Chrome installed locally. This script must be run
# on your own machine -- it cannot be executed inside this chat's sandbox
# (no browser, no outbound access to general websites or Maven Central here).

N="${1:-30}"

echo "Running $N repeated trials. Results will accumulate in test-output/timing-log.csv"

for i in $(seq 1 "$N"); do
  echo "=== Trial $i / $N ==="
  mvn -q -Dtest=cucumberOptions.TestNGTestRunner \
      -Dcucumber.filter.tags="not @tag1" \
      test
done

echo "Done. See test-output/timing-log.csv"
echo "Next: python3 analyze_timings.py"
