cd /d c:\Users\Sarath\Desktop\Spring Boot Projects\org-skills-intelligence-platform
set JAVA_HOME=C:\Users\Sarath\.jdk\jdk-21.0.10
set PATH=C:\Users\Sarath\.jdk\jdk-21.0.10\bin;%PATH%
"C:\Program Files\Apache\Maven\apache-maven-3.9.16\bin\mvn.cmd" -q clean test
echo EXIT:%ERRORLEVEL%
