#!/usr/bin/env bash
set -e
/usr/share/kibana/bin/kibana-plugin install file:///var/lib/netwitness/presidio/elasticsearch/init/kibana-plugins/prelert_swimlane_vis-5.6.9.zip -d /usr/share/kibana/plugins/
unzip -o /var/lib/netwitness/presidio/elasticsearch/init/kibana-plugins/prelert_swimlane_vis-5.6.9.zip -d /usr/share/kibana/plugins/
rm -rf /usr/share/kibana/plugins/prelert_swimlane_vis/*
mv /usr/share/kibana/plugins/kibana/prelert_swimlane_vis-5.6.9/* /usr/share/kibana/plugins/prelert_swimlane_vis/
rm -rf /usr/share/kibana/plugins/kibana/
chown kibana:kibana -R /usr/share/kibana/plugins/