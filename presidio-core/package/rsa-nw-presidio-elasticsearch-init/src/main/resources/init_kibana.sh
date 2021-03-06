#!/usr/bin/env bash

/usr/share/kibana/bin/kibana-plugin install file:///var/lib/netwitness/presidio/elasticsearch/init/kibana-plugins/prelert_swimlane_vis-5.6.9.zip -d /usr/share/kibana/plugins/

if [ "$?" -eq 0 ]
then
unzip -o /var/lib/netwitness/presidio/elasticsearch/init/kibana-plugins/prelert_swimlane_vis-5.6.9.zip -d /usr/share/kibana/plugins/
rm -rf /usr/share/kibana/plugins/prelert_swimlane_vis/*
mv /usr/share/kibana/plugins/kibana/prelert_swimlane_vis-5.6.9/* /usr/share/kibana/plugins/prelert_swimlane_vis/
fi

/usr/share/kibana/bin/kibana-plugin install file:///var/lib/netwitness/presidio/elasticsearch/init/kibana-plugins/kibana_dropdown-5.6.9.zip -d /usr/share/kibana/plugins/
if [ "$?" -eq 0 ]
then
unzip -o /var/lib/netwitness/presidio/elasticsearch/init/kibana-plugins/kibana_dropdown-5.6.9.zip -d /usr/share/kibana/plugins/
rm -rf /usr/share/kibana/plugins/kibana_dropdown/*
mv /usr/share/kibana/plugins/kibana/kibana_dropdown-5.6.9/* /usr/share/kibana/plugins/kibana_dropdown/
fi

rm -rf /usr/share/kibana/plugins/kibana/
chown kibana:kibana -R /usr/share/kibana/plugins/