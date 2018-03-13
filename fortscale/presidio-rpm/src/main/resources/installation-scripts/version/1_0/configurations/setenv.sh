#! /bin/sh
# ==================================================================
# Set garbage collector GC1GC
export CATALINA_OPTS="$CATALINA_OPTS -XX:+UseG1GC"
export CATALINA_OPTS="$CATALINA_OPTS -XX:G1RSetUpdatingPauseTimePercent=5"

# Set maximum memory
export CATALINA_OPTS="$CATALINA_OPTS -Xmx2048m"

