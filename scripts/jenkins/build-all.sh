#### Run Entire Build
#
# 1) Will ensure node/npm, yarn, and ember-cli are
#   installed and with the correct version
# 2) Will then run builds for the many ember apps
#   which includes tests and production builds
#   and re-hosting of style-guide
# 3) Builds RPM for SA
# 4) Puts RPM into YUM repos
#

# For testing purposes
# temp clean up while setting up script
# rm -rf ~/.node

#### INPUTS
# export TESTEM_PORTS="7351, 7352, 7353, 7354, 7355, 7356, 7357, 7358, 7359, 7360"
#   Provides ports for testem to use when running ember tests
# export MOCK_SERVER_PORTS="9985, 9986, 9987, 9988, 9989"
#   Provides ports for the mock server to use when running e2e tests
# export EXTENT=FULL
#   An indicator of to what extent the build should be run.
#   Possible values:
#     FULL: runs everything, tests, builds, RPM, fetch
#     TEST: runs just the tests, no RPM
#     RPM: runs everything, but does not copy RPM out of build
# export FF_ON=future
#   Sets web application features to true. To set multiple features, comma-delimit
#   feature names.
# export FF_OFF=future
#   Sets web application features to false. To set multiple features, comma-delimit
#   feature names.

# turns off noisy jenkins output
# comment this out to debug problematic build
set +x

# Is Master build?
IS_MASTER_BUILD=false
if [[ ("$GIT_BRANCH" == "origin/master") ]]
then
  IS_MASTER_BUILD=true
fi

# bring in utilities and settings
scriptDir="$(dirname "$0")"
. $scriptDir/../_util.sh
. $scriptDir/_settings.sh


export IS_MASTER_BUILD
echo "Is this a master build ${IS_MASTER_BUILD}"
# Should we try building Docker images and run e2e test?
export RUN_E2E_TEST=${RUN_E2E_TEST:-true}
export RUN_UNIT_TESTS=${RUN_UNIT_TESTS:-true}

echo "Run e2e test - ${RUN_E2E_TEST}"
echo "Run unit test - ${RUN_UNIT_TESTS}"

if [ -z ${EXTENT+x} ]
then
  fail "Please export EXTENT variable:"
  fail "export EXTENT=FULL"
  fail "or"
  fail "export EXTENT=RPM"
  fail "or"
  fail "export EXTENT=TEST"
  exit 1
fi

#### Setup environment ####
. $scriptDir/environment.sh

#### Determine which submodules need testing ####
# Sets $submodulesToTest global variable
. $scriptDir/submodule-determination.sh

#### Build Apps ####
. $scriptDir/apps.sh

if [[ $submodulesToTest =~ "|sa|" ]]
then
  if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
  then
    #### Build RPM if running full or rpm build
    . $scriptDir/rpm.sh
    ### If selected, build Docker imaages and push ro DTR
    if [ "${RUN_E2E_TEST}" == "true" ]; then
      . $scriptDir/build-docker-image.sh
    fi
  fi

  if [ "$EXTENT" == "FULL" ]
  then
    #### Moving RPM to Yum directory if running full build
    . $scriptDir/yum.sh
  fi
else
  echo "sa not updated, not building RPM"
fi

#### Victory, poppin bottles
success "Finished Build!"
