# ADDED (Aug 2026 revision) -- Windows PowerShell equivalent of run_repeated_trials.sh
# Runs the Maven test suite N times so test-output/timing-log.csv accumulates
# enough repeated measurements for the paper's Experimental Design section.
# Excludes the live-Flipkart login scenario (@tag1) by default.
#
# Usage (from PowerShell, inside the Cucumber_framework_Final folder):
#   .\run_repeated_trials.ps1 30
#
# If PowerShell blocks the script from running, first run (once, as admin):
#   Set-ExecutionPolicy -Scope CurrentUser RemoteSigned

param(
    [int]$N = 30
)

Write-Host "Running $N repeated trials. Results will accumulate in test-output/timing-log.csv"

for ($i = 1; $i -le $N; $i++) {
    Write-Host "=== Trial $i / $N ==="
    mvn -q "-Dtest=cucumberOptions.TestNGTestRunner" "-Dcucumber.filter.tags=not @tag1" test
}

Write-Host "Done. See test-output/timing-log.csv"
Write-Host "Next: python analyze_timings.py"
