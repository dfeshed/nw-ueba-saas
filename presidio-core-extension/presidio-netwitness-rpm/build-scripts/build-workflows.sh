#!/bin/bash
#---
## build python package presidio workflows extension and distribute into destination folder that is referenced at rpm packaging
#---
echo "removing the default presidio-workflows-extension (from core)"


echo "Current location: $PWD"
BUILD_VENV=$PWD/target/build_env
BUILD_OUTPUT=$PWD/target/dependencies/eggs
echo "creating virtualenv at: $BUILD_VENV"
python -m virtualenv $BUILD_VENV
. $BUILD_VENV/bin/activate;pip uninstall presidio-workflows-extension -y;cd ../../presidio-workflows-extension/;python setup.py bdist_egg --dist-dir ../presidio-core-extension/presidio-netwitness-rpm/target/eggs --build_number=$1
