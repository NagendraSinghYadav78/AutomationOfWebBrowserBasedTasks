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

    # Quick bar chart: mean execution time per scenario with SD error bars
    fig, ax = plt.subplots(figsize=(8, 5))
    ax.bar(summary["scenario"], summary["mean_ms"], yerr=summary["sd_ms"], capsize=5)
    ax.set_ylabel("Mean execution time (ms)")
    ax.set_title(f"Execution time per scenario (n={summary['n'].iloc[0] if len(summary) else 0} runs)")
    plt.xticks(rotation=20, ha="right")
    plt.tight_layout()
    plt.savefig("test-output/execution_time_chart.png", dpi=150)
    print("Wrote test-output/execution_time_chart.png")

    print("\nTo compare against a manual-testing or plain-Selenium baseline, repeat this")
    print("process for each baseline (see baselines list in the paper's Experimental")
    print("Design section) and merge the summaries into the paper's comparison table.")

if __name__ == "__main__":
    main()
