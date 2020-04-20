#!/bin/bash
set -e

# Setting time to enrich the data with new occurrences
if grep -q 'presidio.input.core.transformation.waiting.duration' /etc/netwitness/presidio/configserver/configurations/input-core.properties
	then
		sed -i 's!presidio.input.core.transformation.waiting.duration.*!presidio.input.core.transformation.waiting.duration=P1D!g' /etc/netwitness/presidio/configserver/configurations/input-core.properties
	else
		echo "presidio.input.core.transformation.waiting.duration=P1D" >> /etc/netwitness/presidio/configserver/configurations/input-core.properties
fi