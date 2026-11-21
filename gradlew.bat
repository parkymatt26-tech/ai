@rem SingleCheat Gradle wrapper launcher (Windows)
@if "%DEBUG%"=="" @echo off
setlocal
set APP_HOME=%~dp0
set WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

if defined JAVA_HOME (
  set JAVACMD=%JAVA_HOME%\bin\java.exe
) else (
  set JAVACMD=java
)

if exist "%WRAPPER_JAR%" goto run
where gradle >nul 2>nul
if %ERRORLEVEL%==0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
echo ERROR: gradle/wrapper/gradle-wrapper.jar is missing and no "gradle" command was found.
exit /b 1

:run
"%JAVACMD%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% -classpath "%WRAPPER_JAR%" -Dorg.gradle.appname=gradlew org.gradle.wrapper.GradleWrapperMain %*
