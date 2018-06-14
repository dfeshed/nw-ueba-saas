#! /bin/sh
# ==================================================================
setenv_file="/opt/tomcat/bin/setenv.sh"

if [ "$1" = "--uninstall" ]; then
    # Remove tomcat catalina changes
    perl -pi -e 'BEGIN{undef $/;} s|#\sPRESIDIO\sTOMCAT\sSETTINGS\sSTART.*#\sPRESIDIO\sTOMCAT\sSETTINGS\sEND||gs' $setenv_file

else #Install catalina settings


    echo '# PRESIDIO TOMCAT SETTINGS START' >> $setenv_file
    echo '' >> $setenv_file
    echo '# Set garbage collector GC1GC' >> $setenv_file
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -XX:+UseG1GC"' >> $setenv_file
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -XX:G1RSetUpdatingPauseTimePercent=5"' >> $setenv_file
    echo '' >> $setenv_file
    echo '# Set maximum memory' >> $setenv_file
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Xms2048m"' >> $setenv_file
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Xmx2048m"' >> $setenv_file
    echo '' >> $setenv_file
    echo '# PRESIDIO TOMCAT SETTINGS END' >> $setenv_file


    chmod +777 $setenv_file
    sudo systemctl restart tomcat
fi