#!/usr/bin/env python3
"""
ADDED (Aug 2026 revision): Reads test-output/timing-log.csv (produced by
Hooks.java over repeated runs) and computes mean, standard deviation, median,
and a 95% confidence interval per scenario -- the exact numbers needed for
the paper's execution-time comparison table and Results section.

Usage:
    python3 analyze_timings.py [path/to/timing-log.csv]

Requires: pandas, scipy, matplotlib (pip install pandas scipy matplotlib)
"""
import sys
import pandas as pd
from scipy import stats
import matplotlib.pyplot as plt

def main():
    path = sys.argv[1] if len(sys.argv) > 1 else "test-output/timing-log.csv"
    df = pd.read_csv(path)

    print(f"Loaded {len(df)} rows from {path}\n")

    summary_rows = []
    for scenario, group in df.groupby("scenario"):
        durations = group["duration_ms"]
        n = len(durations)
        mean = durations.mean()
        sd = durations.std(ddof=1) if n > 1 else float("nan")
        median = durations.median()
        if n > 1:
            ci = stats.t.interval(0.95, n - 1, loc=mean, scale=sd / (n ** 0.5))
        else:
            ci = (float("nan"), float("nan"))
        pass_rate = (group["status"] == "PASS").mean() * 100
        summary_rows.append({
            "scenario": scenario,
            "n": n,
            "mean_ms": round(mean, 1),
            "sd_ms": round(sd, 1) if n > 1 else None,
            "median_ms": round(median, 1),
            "ci95_low_ms": round(ci[0], 1) if n > 1 else None,
            "ci95_high_ms": round(ci[1], 1) if n > 1 else None,
            "pass_rate_pct": round(pass_rate, 1),
        })

    summary = pd.DataFrame(summary_rows)
    print(summary.to_string(index=False))
    summary.to_csv("test-output/timing-summary.csv", index=False)
    print("\nWrote test-output/timing-summary.csv")

    # Quick bar chart: mean execution time per scenario with SD error bars.
    # Color-code proposed-framework scenarios vs. the plain Selenium+TestNG
    # baseline (Section 4.4/9) so the comparison is visually clear.
    colors = ['#F9A825' if 'baseline' in s.lower() else '#2E7D32' for s in summary['scenario']]
    fig, ax = plt.subplots(figsize=(9, 5.5))
    ax.bar(summary["scenario"], summary["mean_ms"], yerr=summary["sd_ms"], capsize=5, color=colors)
    ax.set_ylabel("Mean execution time (ms)")
    ax.set_title(f"Execution time per scenario (n={summary['n'].iloc[0] if len(summary) else 0} runs)")
    plt.xticks(rotation=20, ha="right")
    plt.tight_layout()
    plt.savefig("test-output/execution_time_chart.png", dpi=150)
    print("Wrote test-output/execution_time_chart.png")

    baseline_rows = summary[summary['scenario'].str.contains('baseline', case=False)]
    proposed_rows = summary[~summary['scenario'].str.contains('baseline', case=False)]
    if len(baseline_rows) and len(proposed_rows):
        print("\nProposed vs. baseline comparison is now available directly in the table above")
        print("(rows without 'baseline' = proposed framework; rows with 'baseline' = plain")
        print("Selenium+TestNG). Use these paired means/SDs to fill in the paper's Table 4")
        print("comparison and, if desired, run a paired t-test or Wilcoxon signed-rank test")
        print("between matched scenario pairs for statistical significance.")
    else:
        print("\nBaseline rows not found yet -- re-run after baseline.PlainSeleniumBaselineRunner")
        print("has executed at least once (see run-trials.yml).")

if __name__ == "__main__":
    main()
