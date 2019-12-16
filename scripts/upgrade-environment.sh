#!/usr/bin/env bash
set -e

NW_VERSION=$1
ASOC_URL=$2

echo "NW_VERSION=${NW_VERSION}"
echo "ASOC_URL=${ASOC_URL}"
echo -e "https://nw-node-zero/nwrpmrepo" > /etc/netwitness/platform/repobase

#sed -i "s|baseurl=.*baseurl=file:///var/netwitness/common/repo/${NW_VERSION}/OS|g" /etc/yum.repos.d/nw-os-base.repo
#sed -i "s|baseurl=.*baseurl=file:///var/netwitness/common/repo/${NW_VERSION}/RSA|g" /etc/yum.repos.d/nw-rsa-base.repo
yum clean all
rm -rf /var/cache/yum


#### Preparing to update ####
mkdir -p /tmp/upgrade/${NW_VERSION}
cd /tmp/
wget -q ${ASOC_URL}
unzip netwitness-${NW_VERSION}.zip -d /tmp/upgrade/${NW_VERSION}/
upgrade-cli-client --init --version ${NW_VERSION} --stage-dir /tmp/upgrade/
admin_server_ip=$(hostname -I | awk '{print $1}')
echo "admin_server_ip=${admin_server_ip}"
upgrade-cli-client -u --host-addr ${admin_server_ip} --version ${NW_VERSION} -v
sleep 10 && reboot &