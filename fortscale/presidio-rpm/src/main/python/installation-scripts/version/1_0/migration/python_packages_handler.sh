#!/bin/bash

# Delete presidio workflows packages
# Installing is handle in build-workflows.sh

if [ "$1" = "--uninstall" ]; then
    echo "uninstall presidio-workflows-extension"
    pip uninstall presidio-workflows-extension -y
    echo "uninstall presidio-workflows"
    pip uninstall presidio-workflows -y
fi