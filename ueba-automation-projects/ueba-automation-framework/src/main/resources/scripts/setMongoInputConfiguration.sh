#!/bin/bash
set -e

# Change the time field of tls to be event.time:
sed -i 's!"sourceKey": "event_time"!"sourceKey": "time"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json

# netname, org.dst to presidio.netname, presidio.org.dst
sed -i 's!"sourceArrayKey": "presidio_netname"!"sourceArrayKey": "netname"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json
sed -i 's!"sourceKey": "presidio_org_dst"!"sourceKey": "org_dst"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json

# Setting time to enrich the data with new occurrences
if grep -q 'presidio.input.core.transformation.waiting.duration' /etc/netwitness/presidio/configserver/configurations/input-core.properties
	then
		sed -i 's!presidio.input.core.transformation.waiting.duration.*!presidio.input.core.transformation.waiting.duration=P0D!g' /etc/netwitness/presidio/configserver/configurations/input-core.properties
	else
		echo "presidio.input.core.transformation.waiting.duration=P0D" >> /etc/netwitness/presidio/configserver/configurations/input-core.properties
fi