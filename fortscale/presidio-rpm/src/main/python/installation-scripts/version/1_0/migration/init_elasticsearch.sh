#!/usr/bin/env bash
sudo -u presidio bash -c "/usr/local/bin/python /home/presidio/presidio-core/installation/installation-scripts/version/1_0/migration/init_elasticsearch.py --resources_path /home/presidio/presidio-core/el-extensions --run_type core"