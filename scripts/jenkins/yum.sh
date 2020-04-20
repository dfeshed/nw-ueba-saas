#### Artifact archiving into YUM ####
#
# 1) Determines name/directory of YUM folder based on RPM file name
# 2) Removes old YUM file
# 3) Moves new RPM out to YUM directories
#

rpmVersionPatternFile=$SA_RPM_ROOT/RPMS/noarch/*.el7.centos.noarch.rpm
rpmFiles=$SA_RPM_ROOT/RPMS/noarch/*.rpm
ver=$(rpm -qp $rpmVersionPatternFile --qf "%{VERSION}")

major=$(echo $ver| awk -F . '{print $1}')
minor=$(echo $ver| awk -F . '{print $2}')
sp=$(echo $ver| awk -F . '{print $3}')
hf=$(echo $ver| awk -F . '{print $4}')

yumdir=/mnt/libhq-SA/YUM/centos7/RSA/${major}.${minor}/${major}.${minor}.${sp}/${major}.${minor}.${sp}.${hf}

cp -p $rpmFiles $yumdir/.
success "New RPM copied to Yum directory: $yumdir "
