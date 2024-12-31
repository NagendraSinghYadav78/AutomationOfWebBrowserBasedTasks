#Author: nksyadav100@.gmail.com

Feature: Search and Place the order for Products

Scenario: Search Experience for product search in both home and Offers page

Given User is on GreenCart Landing page

When user searched with Shortname "Apple iPhone 15 (Black, 128 GB)" and extracted actual name of product

Then User searched for shortname "Apple iPhone 15 (Black, 128 GB)" in offers page to check if product exist with the same name




    
