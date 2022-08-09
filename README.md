# Budget
This is a budgeting program.  It helps you visualize spending and follow a budget with some work.
That work is categorizing regularly, creating a budget with dollars per day as a measurement, 
creating a savings plan (a percentage basically) and creating 2 more things: a MandatoryMap and a XcldFromCashFlow map.

All of these let you get a hold of your finances and really figure out what's going on as well as give you a guideline,
followed by your plan, to your spending.

To run this program download either the linux branch or the master branch.
If it's the master branch there should be a file called FinanceMac.jar
After creating the appropriate files (in the source code for now) run the program as follows:

java -cp FinanceMac.jar finance.Finance "path to your base directory where you keep all of the input and output files" "path and file to Chase & StarOne files"

There are 2 arguments above in between the <>.

Here are the names of the files:
Input:

Categorize.csv under "baseDirectory"/Categorized directory
  
MandatoryMap.csv under "baseDirectory"/MarkMandatory/
  
XcldFrmCshFlw.csv under "baseDirectory"
  
DollarsPerDayExpenditures.csv under "baseDirectory"
  
and
SavingsPercentages.csv under "baseDirectory"

Ideally and theoretically, after you've created all of these files the program should go through the 
entire sequence.  There is an example of these files in the relative directories.  There are a total of 5 files to create.

If you don't want to load the latest version of Java the linux version works with JRE 11.
The calling of this program is different as well:
java -cp FinanceLinux.jar src.finance.Finance <path a as above> <path b as above>
