#!/bin/bash
#---
## build python package presidio workflows extension and distribute into destination folder that is referenced at rpm packaging
#---
echo "removing the default presidio-workflows-extension (from core)"
pip uninstall presidio-workflows-extension -y

echo "building presidio-workflows-extension"
cd ../../presidio-workflows-extension/
python setup.py bdist_egg --dist-dir ../presidio-core-extension/target/eggs --build_number=$1
echo "finished building presidio-workflows-extension"

