#Author: nksyadav100@.gmail.com

# FIXED (Aug 2026 revision): product name corrected to match the GreenKart
# demo catalog (see searchProduct.feature for the same fix and rationale).

Feature: Add an item to cart

Scenario: User can search and the product to cart

Given User is on Landing page of the application

When user searched with product name "Cucumber"

Then User adds the product to cart
