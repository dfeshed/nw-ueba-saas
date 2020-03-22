#!/bin/bash

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Presidio UI Update Started %%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
ADMIN_SERVER_RPM_BASE_URL="baseurl=http://libhq-ro.rsa.lab.emc.com/SA/YUM/centos7/RSA/11.4/11.4.0/11.4.0.0-dev/"

echo -e "[tier2-rsa-nw-upgrade]\nname=Tier 2 RSA Netwitness Upgrade\ncost=2000\nenabled=1\nfastestmirror_enabled=0\ngpgcheck=1\nsslverify=false\n${ADMIN_SERVER_RPM_BASE_URL}" > /etc/yum.repos.d/tier2-rsa-nw-upgrade.repo
sudo sed -i "s|enabled=.*|enabled=0|g" /etc/yum.repos.d/*.repo
sudo sed -i "s|enabled=.*|enabled=1|g" /etc/yum.repos.d/tier2-rsa-nw-upgrade.repo
yum clean all
rm -rf /var/cache/yum

yum update -y rsa-nw-ui rsa-nw-legacy-web-server

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Presidio UI Update Completed Successfully %%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
