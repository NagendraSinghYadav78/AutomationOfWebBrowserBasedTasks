# Changes made (Aug 2026 revision)

I could not execute this code from the chat sandbox: it has no outbound
network access to general websites (only package registries like npm/PyPI/
GitHub) and no access to Maven Central, so the project can't even be built
there, let alone run a browser against a live site. Everything below is a
source-level fix you'll need to build and run yourself, with a harness set up
so your first real run produces usable data for the paper.

## 1. Target-site mismatch (correctness fix)

`GreenKartStepDefinition.java` and `AddtoCart.java` were named/labeled for
"GreenCart"/"GreenKart" (per the class names and feature-file text: *"User is
on GreenCart Landing page"*), but the actual code launched Selenium against
**`https://www.flipkart.com/`** — a live, real commercial site — using
Flipkart-specific CSS classes (`Pke_EE`, `KzDlHZ`, etc.) and, in `AddtoCart`,
a hardcoded pincode.

**Fixed:** both classes now target the real GreenKart demo application,
`https://rahulshettyacademy.com/seleniumPractise/#/`, which is a
purpose-built Selenium/Cucumber practice sandbox — safe to run repeatedly for
the paper's repeated-measures experiment. Feature-file product names were
changed from `"Apple iPhone 15 (Black, 128 GB)"` (a Flipkart product) to
`"Cucumber"` (an actual GreenKart product).

**You must verify the locators** (`search-keyword`, `div.product`,
`h4.product-name`, `ADD TO CART` button text, `a.cart-icon`, `PROCEED TO
CHECKOUT`) against the live DOM before your first real run — I could not
confirm them by executing a browser against the site from this sandbox. They
reflect the site's well-documented structure from public tutorials, but
markup can drift.

## 2. `Mylogin.java` kept targeting live Flipkart — on purpose

The login/OTP scenario has no equivalent on the GreenKart demo (no email-OTP
login flow there), so it's left pointed at real Flipkart. Added a comment:
**do not** include this scenario in your repeated 30x timing loop — treat it
as a functional check, not a benchmark, since it hits production
authentication infrastructure.

## 3. Exposed API key (security fix)

`Mylogin.java` had a live-looking Mailosaur API key hardcoded and committed
to your public GitHub repo:
```
String apiKey = "BvcPY3K0oEcBrCkMiYfQzydzolNhUn3W";
```
**Rotate/revoke this key in your Mailosaur dashboard now** — treat it as
compromised regardless of anything else here. The code now reads it from an
environment variable instead:
```
export MAILOSAUR_API_KEY=your_new_key_here
```

## 4. Pre-existing bug fix

`Mylogin.java` had a malformed XPath string missing its closing `]`, which
would throw `InvalidSelectorException` at runtime instead of the intended
wait/timeout behavior. Fixed.

## 5. Added: automatic timing harness (`Hooks.java`)

New Cucumber `@Before`/`@After` hooks record `System.nanoTime()` around every
scenario and append one row (timestamp, scenario name, pass/fail, duration in
ms) to `test-output/timing-log.csv`. This is what the paper's Experimental
Design section (RQ1/RQ4) needs — repeated measurements per scenario.

## 6. Added: `run_repeated_trials.sh` / `run_repeated_trials.ps1`

Runs the suite N times (default 30) via Maven, excluding the live-Flipkart
login scenario by tag filter. Run on your own machine.

macOS/Linux:
```
chmod +x run_repeated_trials.sh
./run_repeated_trials.sh 30
```

Windows PowerShell:
```
.\run_repeated_trials.ps1 30
```

## 7. Added: `analyze_timings.py`

Reads `test-output/timing-log.csv`, computes mean / SD / median / 95% CI per
scenario, writes `test-output/timing-summary.csv`, and generates
`test-output/execution_time_chart.png` — a real bar chart from your real
data, ready to drop into the paper in place of a screenshot.
```
pip install pandas scipy matplotlib
python3 analyze_timings.py
```

## 8. Added: `.github/workflows/run-trials.yml` (recommended — no local install needed)

A GitHub Actions workflow that runs the repeated trials on GitHub's own
servers, which have Java, Maven, and Chrome pre-installed and normal internet
access — unlike the AI sandbox that prepared these files.

**Setup (one time):**
1. Commit this repo (including the `.github/workflows/` folder) to your
   GitHub repo.
2. On GitHub: your repo → **Actions** tab → **Run Timing Trials** →
   **Run workflow** → set `trials` to e.g. `30` → **Run workflow**.
3. Wait for it to finish (a few minutes for 30 trials), open the completed
   run, and download the **timing-results** artifact — a zip with
   `timing-log.csv`, `timing-summary.csv`, and `execution_time_chart.png`
   already computed.
4. Send me that artifact (or just the summary numbers) and I'll fill in the
   paper's tables and swap in the real chart.

This is free for public repos, and GitHub gives private repos a free monthly
minutes allowance too — 30 trials of these lightweight scenarios should use
well under it.

## What's still on you

- Verify the GreenKart locators against the live DOM.
- Rotate the Mailosaur key.
- Actually run the trials (this sandbox can't).
- Add a plain-Selenium/TestNG baseline (no Cucumber layer) for the RQ1/RQ2
  comparison — currently only the proposed framework is instrumented.
- Send me the resulting CSV/PNG (or just the numbers) and I'll fill in the
  paper's placeholder tables and swap in the real chart.
