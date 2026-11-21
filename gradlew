#!/bin/sh
# ----------------------------------------------------------------------------
# SingleCheat Gradle wrapper launcher
# ----------------------------------------------------------------------------
set -e

APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD=java
fi

if [ -f "$WRAPPER_JAR" ]; then
  exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS \
    -classpath "$WRAPPER_JAR" \
    -Dorg.gradle.appname=gradlew \
    org.gradle.wrapper.GradleWrapperMain "$@"
fi

# Fallback: a system Gradle installation
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "ERROR: gradle/wrapper/gradle-wrapper.jar is missing and no 'gradle' command was found." >&2
echo "Install Gradle 8.10+ (https://gradle.org/install/) or run 'gradle wrapper --gradle-version 8.10'." >&2
exit 1
