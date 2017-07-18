#!/bin/bash
#---
## build python package presidio workflows and distribute into destination folder that is referenced at rpm packaging
#---
echo "building presidio-workflows"
cd ../../presidio-workflows/
python setup.py bdist_egg --dist-dir ../fortscale/target/eggs
echo "finished building presidio-workflows"
