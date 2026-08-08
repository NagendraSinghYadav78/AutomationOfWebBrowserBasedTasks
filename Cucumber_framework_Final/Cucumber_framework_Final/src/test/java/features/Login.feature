@tag
Feature: Login
  I want to use this template for my feature file

  @tag1
  Scenario: Successful login to Flipkart
    Given User is on the homepage of Flipkart
    When user enters valid email "stand-morning@46m71knb.mailosaur.net" in Enter Email / mobile number field
    And user clicks on request OTP button
    Then user should be redircted to login page
    And account name should be displayed 
    
    
