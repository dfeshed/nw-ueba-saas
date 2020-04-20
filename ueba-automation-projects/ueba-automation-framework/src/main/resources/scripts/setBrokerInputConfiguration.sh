#!/bin/bash
set -e

# Change the time field of tls to be event.time:
sed -i 's!nwTlsAgent.sources.sdkSource.timeField.*!nwTlsAgent.sources.sdkSource.timeField=event.time!g' /var/netwitness/presidio/flume/conf/adapter/tls.properties
sed -i 's!sessionid,time,ip!sessionid,time,event.time,ip!g' /var/netwitness/presidio/flume/conf/adapter/tls.properties
sed -i 's!"sourceKey": "time"!"sourceKey": "event_time"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json


# netname, org.dst to presidio.netname, presidio.org.dst
sed -i 's!alias.host,org.dst,asn.dst,payload.req,payload.res,netname,ja3!alias.host,presidio.org.dst,asn.dst,payload.req,payload.res,presidio.netname,ja3!g' /var/netwitness/presidio/flume/conf/adapter/tls.properties
sed -i 's!multiValued=alias.host,netname,ssl.ca!multiValued=alias.host,presidio.netname,ssl.ca!g' /var/netwitness/presidio/flume/conf/adapter/tls.properties
sed -i 's!"sourceArrayKey": "netname"!"sourceArrayKey": "presidio_netname"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json
sed -i 's!"sourceKey": "org_dst"!"sourceKey": "presidio_org_dst"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json


# Setting time to enrich the data with new occurrences
if grep -q 'presidio.input.core.transformation.waiting.duration' /etc/netwitness/presidio/configserver/configurations/input-core.properties
	then
		sed -i 's!presidio.input.core.transformation.waiting.duration.*!presidio.input.core.transformation.waiting.duration=P1D!g' /etc/netwitness/presidio/configserver/configurations/input-core.properties
	else
		echo "presidio.input.core.transformation.waiting.duration=P1D" >> /etc/netwitness/presidio/configserver/configurations/input-core.properties
fi