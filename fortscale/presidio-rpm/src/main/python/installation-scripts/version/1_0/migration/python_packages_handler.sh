#!/bin/bash

# Delete presidio workflows packages
# Installing is handle in build-workflows.sh

if [ "$1" = "--uninstall" ]; then
    echo "uninstall presidio-workflows-extension"
    /usr/local/bin/pip uninstall presidio-workflows-extension -y
    echo "uninstall presidio-workflows"
    /usr/local/bin/pip uninstall presidio-workflows -y
fi