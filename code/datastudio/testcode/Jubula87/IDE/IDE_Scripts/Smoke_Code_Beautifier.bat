@echo off
if exist "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_Code_beautifier" (
    echo "hi"
)else (
	mkdir "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_Code_beautifier"
)

cd /d C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Scripts\Config_Files\
call Starttestsuite_Smoke_Code_beautifier.bat

cd "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_Code_beautifier"
del *junit*.xml

cd /d C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Scripts\Config_Files\

java -jar XMLParser.jar
pause