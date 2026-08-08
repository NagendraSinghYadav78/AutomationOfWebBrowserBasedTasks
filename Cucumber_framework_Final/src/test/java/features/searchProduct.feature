#Author: nksyadav100@.gmail.com

# FIXED (Aug 2026 revision): "Apple iPhone 15 (Black, 128 GB)" was a Flipkart
# product name, but GreenKart (the site this scenario actually targets) is a
# vegetable/fruit store -- the mismatch is corrected below.

Feature: Search and Place the order for Products

Scenario: Search Experience for product search in both home and Offers page

Given User is on GreenCart Landing page

When user searched with Shortname "Cucumber" and extracted actual name of product

Then User searched for shortname "Cucumber" in offers page to check if product exist with the same name
