"# budevices_dd"

-- Example running the tests from command prompt

1. Open command prompt and navigate to project directory

NOTE: Your project path will be different<br/>
cd C:\Users\TPD_Auto\Desktop\Noor\AutomationProjects\JavaProject\MHRA_MDCM_DEVICES_DD

2. Run one of the commands below:

IE:<br/>
mvn clean test -Dtest=RunAllSmokeTest -Dcurrent.browser=ie -Dspring.profiles.active=mhratest
<br/>
GC:<br/>
mvn clean test -Dtest=RunAllSmokeTest -Dcurrent.browser=gc -Dspring.profiles.active=mhratest
<br/>
FF:<br/>
mvn clean test -Dtest=RunAllSmokeTest -Dcurrent.browser=ff -Dspring.profiles.active=mhratest
<br/>
PhantomJS Headless :<br/>
mvn clean test -Dtest=RunAllSmokeTest -Dcurrent.browser=pjs -Dspring.profiles.active=mhratest
<br/>


mvn surefire-report:report
 <br/>
mvn clean test surefire-report:report
 <br/>

Create Better Reports With These Commands: <br/>
# Run tests and generate .xml reports
mvn test
# Convert .xml reports into .html report, but without the CSS or images
mvn surefire-report:report-only

# Put the CSS and images where they need to be without the rest of the
# time-consuming stuff
mvn site -DgenerateReports=false

mvn site : is very slow
mvn surefire-report:report-only + mvn site -DgenerateReports=false = faster solution
