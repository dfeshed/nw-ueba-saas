#!/usr/bin/env bash
set -e

NW_VERSION=$1

echo "  #############  Starting upgrade-repo-configuration.sh #############"

echo "NW_VERSION=${NW_VERSION}"
sed -i "s|baseurl=.*|baseurl=https://nw-node-zero/nwrpmrepo/${NW_VERSION}/OS|g" /etc/yum.repos.d/nw-os-base.repo
sed -i "s|baseurl=.*|baseurl=https://nw-node-zero/nwrpmrepo/${NW_VERSION}/RSA|g" /etc/yum.repos.d/nw-rsa-base.repo
OWB_ALLOW_NON_FIPS=on yum clean all
rm -rf /var/cache/yum

echo "  #############  upgrade-repo-configuration.sh DONE #############"
