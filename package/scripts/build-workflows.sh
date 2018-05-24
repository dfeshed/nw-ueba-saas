#!/bin/bash
#---
## build python package presidio workflows and distribute into destination folder that is referenced at rpm packaging
#---
echo "Current location: $PWD"
BUILD_VENV=$PWD/target/build_env
BUILD_OUTPUT=$PWD/target/dependencies/eggs
echo "creating virtualenv at: $BUILD_VENV"
python -m virtualenv $BUILD_VENV
. $BUILD_VENV/bin/activate

echo "building presidio_extension"
cd ../presidio-workflows-extension/
python setup.py bdist_egg --dist-dir $BUILD_OUTPUT --build_number=$1
echo "finished building presidio_extension"
