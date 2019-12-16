#!/usr/bin/env bash
set -e

NW_VERSION=$1
ASOC_URL=$2

echo "  #############  Starting upgrade-repo-configuration.sh #############"

echo "NW_VERSION=${NW_VERSION}"
echo "ASOC_URL=${ASOC_URL}"
echo -e "https://nw-node-zero/nwrpmrepo" > /etc/netwitness/platform/repobase

sed -i "s|baseurl=.*|baseurl=file:///var/netwitness/common/repo/${NW_VERSION}/OS|g" /etc/yum.repos.d/nw-os-base.repo
sed -i "s|baseurl=.*|baseurl=file:///var/netwitness/common/repo/${NW_VERSION}/RSA|g" /etc/yum.repos.d/nw-rsa-base.repo

yum clean all
rm -rf /var/cache/yum

echo "  #############  upgrade-repo-configuration.sh DONE #############"
