#!/bin/bash
#---
## build python package presidio workflows and distribute into destination folder that is referenced at rpm packaging
#---
echo "building presidio_extension"
cd ../../../presidio-workflows-extension/
python setup.py bdist_egg --dist-dir ../fortscale/target/eggs --build_number=$1
echo "finished building presidio_extension"

echo "building presidio-workflows"
cd ../presidio-workflows/
python setup.py bdist_egg --dist-dir ../fortscale/target/eggs --build_number=$1
echo "finished building presidio-workflows"

