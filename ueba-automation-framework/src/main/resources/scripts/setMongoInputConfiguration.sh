#!/bin/bash
set -e

# Change the time field of tls to be event.time:
sed -i 's!"sourceKey": "event_time"!"sourceKey": "event"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json

# netname, org.dst to presidio.netname, presidio.org.dst
sed -i 's!"sourceArrayKey": "presidio_netname"!"sourceArrayKey": "netname"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json
sed -i 's!"sourceKey": "presidio_org_dst"!"sourceKey": "org_dst"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json