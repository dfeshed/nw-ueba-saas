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