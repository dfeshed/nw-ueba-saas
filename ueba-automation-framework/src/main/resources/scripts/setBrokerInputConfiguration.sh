#!/bin/bash
set -e

# Change the time field of tls to be event.time:
sed -i 's!nwTlsAgent.sources.sdkSource.timeField.*!nwTlsAgent.sources.sdkSource.timeField=event.time!g' /var/netwitness/presidio/flume/conf/adapter/tls.properties
sed -i 's!sessionid,time,ip!sessionid,time,event.time,ip!g' /var/netwitness/presidio/flume/conf/adapter/tls.properties
sed -i 's!"sourceKey": "time"!"sourceKey": "event_time"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json
