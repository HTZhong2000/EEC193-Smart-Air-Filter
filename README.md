# EEC193 Smart Air Filter
Here are the codes of EEC193 Smart Air Filter project from team RSQ @angl19 <br/>
We have four parts of code in the project
## APP
For APP code, we have a Main acvitivity file and several fragment files for each function in the app
* Condition ----- Display real-time data and control the fan
* Home ----- Homepage code
* Login ----- Login system
* options ----- For user to log out
* Records ----- Display long-term records and real-time sensor data charts
* Reservation ----- Set the strategy of smart filter
* Signup ----- Sign up system
* Verification ----- Set the email verification function during login
## AWS
For AWS code, we have several Python files operating in the AWS Lambda, triggered by HTTP request and eventtrigger
* EEC193_GETDATA ----- Get real-time sensor data from database
* EEC193_GETREC ----- Get sensor data records from database
* EEC193_Getplan ----- Get the current strategy for air filter
* EEC193_LOGIN ----- Receive username and password from user and verify them for both login and signup functions
* EEC193_POST ----- Send sensor data and get operation status from air filter to database
* EEC193_SCHEDULE ----- Set the strategy for air filter
* EEC193_Threshold ----- Automatically check the sensor data and control the air filter
* EEC193_Update ----- Update the real-time sensor data into the records database
* EEC193_Verify ----- Manage the verification process during the login

## Web
HTML files built for each pages on the website

## MCU
Arduino codes for each module
* ESP01_WIFI ----- Code of WiFi module that send HTTP request and get response
* Mainmodule ----- Code of Arduino on the air filter that control the fan and torchscreen
* Submodule ----- Code of Arduino on the sensor module that get sensor data and send to main module
