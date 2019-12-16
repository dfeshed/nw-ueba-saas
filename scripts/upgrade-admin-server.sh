#!/usr/bin/env bash
set -e

NW_VERSION=$1
ASOC_URL=$2

echo "NW_VERSION=${NW_VERSION}"
echo "ASOC_URL=${ASOC_URL}"
echo -e "https://nw-node-zero/nwrpmrepo" > /etc/netwitness/platform/repobase

sed -i "s|baseurl=.*|baseurl=file:///var/netwitness/common/repo/${NW_VERSION}/OS|g" /etc/yum.repos.d/nw-os-base.repo
sed -i "s|baseurl=.*|baseurl=file:///var/netwitness/common/repo/${NW_VERSION}/RSA|g" /etc/yum.repos.d/nw-rsa-base.repo

yum clean all
rm -rf /var/cache/yum


#### Preparing to update ####
mkdir -p /tmp/upgrade/${NW_VERSION}
cd /tmp/

echo "  #############  Starting download upgrade ZIP #############"
wget -q --show-progress ${ASOC_URL}
echo "  #############  upgrade ZIP Download finished #############"

unzip netwitness-${NW_VERSION}.zip -d /tmp/upgrade/${NW_VERSION}/
echo
echo "  #############  upgrade-cli-client init started #############"
upgrade-cli-client --init --version ${NW_VERSION} --stage-dir /tmp/upgrade/
echo "  #############  upgrade-cli-client init done #############"

admin_server_ip=$(hostname -I | awk '{print $1}')
echo
echo "  #############  Going to run upgrade on admin-server=${admin_server_ip} #############"
upgrade-cli-client -u --host-addr ${admin_server_ip} --version ${NW_VERSION} -v
echo "  #############  Done. #############"
echo
echo "  #############  Going to REBOOT in 10 seconds !!! #############"
sleep 10 && reboot &

#https://wiki.na.rsa.net/display/PRES/Netwitness+Environment+Upgrade