Background
==========

Primer
------
IPUMS-CPS is described as "Harmonized data on people in the Current Population Survey, every March from 1962 to the present". They have done the heavy lifting of creating a system for unified data extract from the government's published Current Population Survey (CPS) data. Unfortunately, the IPUMS data is lacking the unique identifiers required for matching records from one year to the next.

This application
----------------
This application is specific for creating matched records. You could modify the code to just create non-anonymous records from the IPUMS data, but this application takes it another step and uses those identified records to create matched records year-to-year through the IPUMS/CPS data.

Usage - Creating and Downloading an IPUMS Data Extract
======================================================

(First Time only) Register for an Account
-----------------------------------------
Go to: http://cps.ipums.org/cps-action/users/login
Type in your email address, click 'Submit'. You will be taken to a registration page. Fill in your details and check your email. You should then be able to sign in.

Sign In
-------
Go to this page again: http://cps.ipums.org/cps-action/users/login
This time, when you type your email address, it should have a box for your password.

Select Data Variables for Analysis
----------------------------------
Go to: http://cps.ipums.org/cps-action/variables/group
Choose the variables you wish to analyze by clicking their section and then placing a checkmark next to them.
You must include: LINENO, AGE, RACE, SEX.
When done, choose the green "View Cart" button.

Add Samples
-----------
Then click, "Add more Samples".
(select all, usually)
Click "Submit sample selections".

Checking out...
---------------
Now there should be a large green "Check out: Create Data Extract button". Press it.

Choose "Rectangular" (VS) "Hierarchical".

You shouldn't need to select anything on this page, just "Continue to next step"

Scroll down to confirm everything looks okay and choose "Submit extract".

It will show you a confirmation page and will tell you when your download is available.

Downloading the Data Extract
----------------------------
When you receive an email alerting you that your extract is ready, follow this link: http://cps.ipums.org/cps-action/extract_requests/download

The extracts are listed in reverse order, with the newest extractions at the top. From your extraction, you will need to download the file marked 'data' (this file will be quite large), and the file marked 'SPS'. Note where you have downloaded these as you will need to provide them to this application.

Note, when downloading the SPS file, you may need to Right Click->Save (Link|Target) As...

Using the Program
=================

You'll need to construct a database first. Minimal commands necessary:

`mysql -u root -p`

`create database cps`

`grant all on cps.* to 'cps' @localhost identified by 'cps';

To run the program, you need to build a runnable JAR file.  Open the source code in your IDE (I use Eclipse).  Right-click the "cpsmatcher" project and click "Export".  Select "Runnable JAR file" and click next.  Continue through the dialogue, specifying where you want the output jar.

To run the cpsmatcher.jar file, simply type "java -jar cpsmatcher.jar" in the command line wherever you put your output .jar file.  

CAUTIONS
========

Raw CPS Data Files
------------------
Please ensure that you use files from this page: http://www.nber.org/data/current-population-survey-data.html
Specifically the ones whose URLs resemble: http://www.nber.org/cps/cpsmarXX.zip (There are a few years that have missing links. The zips exist, try manipulating the URL)