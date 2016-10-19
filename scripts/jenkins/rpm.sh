#### RPM Generation ####
#
# 1) Create all the necessary boilerplate RPM directories
# 2) Copies assets into those directories from sa build
# 3) Creates RPM .spec file
# 4) Runs rpmbuild
#

function cleanAndMakeDirs {
  for dir in "$@"
  do
    if [[ $(pathExists $1) == "true" ]]
    then
      rm -rf $dir
    fi
    mkdir -p $dir
    info "Created $dir"
  done
}

function writeRPMSpecFile {
  info "Writing spec file to $RPM_SPEC_FILE"
  scriptDir="$(dirname "$0")"
  sed $scriptDir/rpm.spec.template -e "s@STABILITY@$STABILITY_ID@g" -e "s@NETWITNESS_VERSION@$NETWITNESS_VERSION@g" -e "s@GIT_COMMIT@${GIT_COMMIT:0:10}@g" -e "s@DATE@$1@g" -e "s@TMP_RPM_BUILD_ROOT@$TMP_RPM_BUILD_ROOT@g" -e "s@RPM_BUILD_ROOT@$RPM_BUILD_ROOT@g" -e "s@VERSION@$2@g" > $RPM_SPEC_FILE
}

function makeRPMDirs() {
    cleanAndMakeDirs $RPM_BUILD_ROOT $TMP_RPM_BUILD_ROOT/etc $TMP_RPM_BUILD_ROOT/opt/rsa/sa-ui $SA_RPM_ROOT/BUILD $SA_RPM_ROOT/RPMS $SA_RPM_ROOT/SOURCES $SA_RPM_ROOT/SPECS $SA_RPM_ROOT/SRPMS
}

function buildRPM() {
    # make RPM out directories and working directories
    # and move assets into RPM dirs
    info "********************"
    info "Begin RPM Processing"

    # create .spec file for RPM
    writeRPMSpecFile $1 $2

    # Run the RPM
    cd $SA_RPM_ROOT/BUILD
    info "Building RPM"
    rpmbuild -bb --buildroot $RPM_BUILD_ROOT --define "_topdir $SA_RPM_ROOT" --define "_builddir $SA_RPM_ROOT/BUILD" --define "_rpmdir $SA_RPM_ROOT/RPMS" --define "_sourcedir $SA_RPM_ROOT/SOURCES" --define "_specdir $SA_RPM_ROOT/SPECS" --define "_srcrpmdir  $SA_RPM_ROOT/SRPMS" $RPM_SPEC_FILE
    checkError "RPM build has failed"
    mv $SA_RPM_ROOT/RPMS/noarch/*.rpm $SA_RPM_ROOT
    cd $CWD
}

rm -f $SA_RPM_ROOT/*.rpm
timestamp="$(date +%y%m%d%H%M%S)"

# Build EL6 RPM
makeRPMDirs
cp -rf $SA_ROOT/rpm/nginx $TMP_RPM_BUILD_ROOT/etc/nginx
cp -rf $SA_ROOT/dist $TMP_RPM_BUILD_ROOT/opt/rsa/sa-ui/html
buildRPM $timestamp "el6"

# Build EL7 RPM
makeRPMDirs
cp -rf $SA_ROOT/dist $TMP_RPM_BUILD_ROOT/opt/rsa/sa-ui/html
buildRPM $timestamp "el7"

mv $SA_RPM_ROOT/*.rpm $SA_RPM_ROOT/RPMS/noarch

success "RPM build successful"
info "********************"
