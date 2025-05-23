![portfolio-2](https://github.com/drbenjaminlouis/mess-master/assets/64739511/778f5699-e89c-4b3b-b953-837c08d15753)

## Project Synopsis

Mess Master is a mobile application designed to revolutionize food mess management in hostels, hotels, colleges, and similar institutions. It streamlines meal preparation, tracks consumption patterns, and elevates the dining experience by leveraging technology and user-centric design.

## Purpose

The project aims to address inefficiencies in conventional food mess management systems by automating administrative tasks, providing real-time insights, and enhancing accountability. Mess Master seeks to improve resource utilization, user satisfaction, and operational efficiency.

## Goals

- **Efficient Mess Management:** Optimize resources and minimize wastage through automation.
- **Enhanced User Experience:** Offer personalized meal planning and real-time insights.
- **Streamlined Administration:** Automate tasks like user management and meal planning.
- **Improved Accountability:** Provide insights into user preferences and operational performance.
- **Customizable Settings:** Adapt app preferences to different organizational needs.

## Intended Audience

- **Administrators and Managers:** Those overseeing food mess systems in hostels, hotels, and colleges.
- **Educational Institutions:** College administrators managing on-campus dining services.

## Project Objectives

1. **Efficient Mess Management:** Automate meal planning, inventory management, and resource allocation.
2. **Enhanced User Experience:** Provide personalized meal plans and nutritional information.
3. **Streamlined Administration:** Automate workflows for user management and system settings.
4. **Improved Accountability:** Track user preferences, consumption patterns, and operational metrics.

## Project Outline

### Modules

1. **User Management:** Manage customer accounts, preferences, and meal plans.
2. **Meal Planning:** Estimate meal quantities based on anticipated attendance.
3. **Dashboard:** Monitor key metrics like customer enrollment and consumption trends.
4. **Settings:** Customize notifications, data management, and other app preferences.
5. **Plan Management:** Add, delete, and update meal plan details.

## Configuring Email Credentials

To enable email functionality in the Mess Master application for sending OTPs or notifications, follow these steps to configure your Gmail credentials:

1. **Open `ChangePassword.kt`:**
    - Navigate to the file `app/src/main/java/com/example/messmaster/ChangePassword.kt` in your Kotlin codebase.

2. **Provide Gmail Credentials:**
    - Locate the `sendOTPEmail` function within `ChangePassword.kt`.

      ```kotlin
      private fun sendOTPEmail(receiverEmail: String, otp: String) {
          val senderEmail = "your_email@gmail.com" // Replace with your Gmail address
          val password = "your_app_password" // Replace with your Gmail app-specific password
 
          // Existing code for sending email...
      }
      ```

3. **Replace Placeholder Values:**
    - Replace `"your_email@gmail.com"` with your Gmail account email address.
    - Replace `"your_app_password"` with your Gmail [app-specific password](https://support.google.com/accounts/answer/185833).


## Prototype

Click [here](https://www.figma.com/proto/eb9Q50cHDrHLRlljyGyyew/MESS-MASTER?node-id=0-1&t=hV7HOAIhfE06zhaY-1) to view the prototype of Mess Master.

## User Guide

### Installation Instructions

1. **Download:** Get Mess Master apk file from the github repository.
2. **Install:** Follow on-screen instructions to install the app.
3. **Open:** Launch Mess Master from your app drawer or home screen.

### User Interface Guide

#### Login

1. Open the app and navigate to the login screen.
2. Enter your registered email address and password in the respective input fields.
3. Tap the "Login" button to access the app.
4. New users can sign up by selecting the "Signup" option and providing their name, email, and a password of their choice.

#### Signup

1. If you're a new user, select the "Signup" option from the login screen.
2. Enter your name, email address, and a secure password in the provided input fields.
3. Tap the "Signup" button to create a new account.
4. Upon successful signup, you'll be navigated to the home screen.

#### Home (Dashboard Insights)

1. After logging in, you'll be directed to the home screen.
2. The home dashboard provides insights such as total customers, active and inactive customers, and the number of students for breakfast, lunch, and dinner.
3. Additionally, the dashboard displays a list of customers whose plans are expiring today.

#### Customer Management

1. In the customer management section, you'll find a list of all customers.
2. Clicking on the floating action button in the bottom right corner provides options for adding, removing, and editing customers.
3. To add a new customer, click on "Add Customer" and enter the customer's name, mobile number, and select a plan.
4. To edit a customer's details, enter the mobile number, and fetch the data. Then, update the name and plan as needed.
5. To remove a customer, enter the mobile number, fetch the data, and click on the remove button to delete the customer from the database.

#### Plan Management

1. The plan management section displays a list of all plans.
2. Clicking on the floating action button provides options for adding, removing, and editing plans.
3. When adding a new plan, enter the plan name, price, and select the validity period. Plan names should start with either BLD -, BL -, BD -, or LD -.
4. To edit a plan, select the plan from the drop-down menu, update the price and validity as required.
5. To remove a plan, select the plan, fetch the plan data, and click on the remove button to delete the plan.

#### Settings

1. Access the settings menu by tapping on the settings icon or navigating to the settings screen.
2. Toggle the notifications switch to enable or disable notifications as per your preference.
3. Use the toggle button to switch between data saver mode for the app.
4. If you wish to reset the app data, locate the "Reset App Data" option, and tap on it to initiate the reset process.

#### Change Password

1. To change your password, navigate to the change password screen within the settings menu.
2. Tap the "Generate OTP" button to receive a one-time password (OTP) on your registered email address.
3. Enter the OTP in the designated input field.
4. Enter your new password and confirm the new password. Finally, tap the "Change Password" button.

## Conclusion

Developing Mess Master provided valuable lessons in software development, project management, and technical skill enhancement. The project emphasized the importance of adaptability, collaboration, and continuous learning, preparing the team for future challenges with confidence and agility.
