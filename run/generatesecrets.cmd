@echo off

set JAVA_BIN="C:\Program Files\Java\jdk-15.0.1\bin"
set PROJECT=E:\carolinaadvancedsoftware\cass-secrets
set LIBS=%PROJECT%\run\libs
set JAR=%LIBS%\cass-secrets-0.0.1-SNAPSHOT-boot.jar

%JAVA_BIN%\java.exe -cp %LIBS% -jar %JAR% generatesecrets %*

