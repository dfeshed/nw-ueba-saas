#### Artifact archiving into YUM ####
#
# 1) Determins name/directory of YUM folder based on RPM file name
# 2) Removes old YUM file
# 3) Moves new RPM out to YUM directories
#

rpmFile=$SA_RPM_ROOT/RPMS/noarch/*.rpm
ver=$(rpm -qp $rpmFile --qf "%{VERSION}")

major=$(echo $ver| awk -F . '{print $1}')
minor=$(echo $ver| awk -F . '{print $2}')
sp=$(echo $ver| awk -F . '{print $3}')
hf=$(echo $ver| awk -F . '{print $4}')

yumdir=/mnt/libhq-SA/YUM/RSA/${major}.${minor}/${major}.${minor}.${sp}/${major}.${minor}.${sp}.${hf}

rm -rf $yumdir/sa-ui-client-*
info "Removed existing yum sa-ui-client RPM(s)"

cp -p $rpmFile $yumdir/.
success "New RPM copied to Yum directory: $yumdir "