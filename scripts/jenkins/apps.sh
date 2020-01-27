if [ -z ${TESTEM_PORTS+x} ]
then
  fail "Must export TESTEM_PORTS variable before running script"
  exit 1
fi

if [ -z ${MOCK_SERVER_PORTS+x} ]
then
  fail "Must export MOCK_SERVER_PORTS variable before running script"
  exit 1
fi

function doTestApp {
  local _need="false"
  if [[ $submodulesToTest =~ "|$1|" ]]
  then
    _need="true"
  fi

  # Check if we just want to test everything regardless
  # of what the build determined should be tested
  if [ "${TEST_EVERYTHING}" == "true" ]
  then
    _need="true"
  fi

  echo $_need
}

# Turn strings into arrays
# cannot export arrays at command line
IFS=', ' read -r -a TESTEM_PORTS_ARRAY <<< "$TESTEM_PORTS"
IFS=', ' read -r -a MOCK_SERVER_PORTS_ARRAY <<< "$MOCK_SERVER_PORTS"

function runEmberTestWithMockServer {
  local mockPort=${MOCK_SERVER_PORTS_ARRAY[$RANDOM % ${#MOCK_SERVER_PORTS_ARRAY[@]} ]}
  local testemPort=${TESTEM_PORTS_ARRAY[$RANDOM % ${#TESTEM_PORTS_ARRAY[@]} ]}

  info "Starting Express mock test server for $1"

  # run mock server
  RESPONSE_DELAY=1 MOCK_PORT=$mockPort node mockserver.js &
  checkError "Mock server for $1 refused to start"
  local PID=$!
  success "$1 mock server started, process id: $PID"

  local SHOULD_COVERAGE=$2
  if [ "${IS_MASTER_BUILD}" == "false" ]
  then
    # don't want to run coverage in a PR build no matter what
    SHOULD_COVERAGE="false"
  fi

  # now run the tests
  info "Running 'ember exam' for $1 on port $testemPort"
  info "COVERAGE=$SHOULD_COVERAGE NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF MOCK_PORT=$mockPort node --max_old_space_size=$3 ./node_modules/.bin/ember exam --split=4 --parallel --test-port $testemPort"
  COVERAGE=$SHOULD_COVERAGE NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF MOCK_PORT=$mockPort node --max_old_space_size=$3 ./node_modules/.bin/ember exam --split=4 --parallel --test-port $testemPort
  local status=$?

  # kill mock server
  kill -9 $PID

  if [[ $status != "0" ]]
  then
    fail "$1"
    fail "Exiting..."
    exit $status
  fi

  success "'ember exam' for $1 was successful"
  # since we are using ember exam with parallel and split=4 flags, the code-coverage library would
  # generate four directories coverage_<1>, coverage_<2>, coverage_<3> and coverage_<4>
  # Merge these directories to create a single coverage direcory
  # Push the newly generated coverage directory to the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
  # TODO: Coverage is only enabled for master builds. Expand the functionality to PR builds later.
  if [ "${IS_MASTER_BUILD}" == "true" ]
  then
    ember coverage-merge
    info "Copying the coverage directory for '$1' from workspace to mount."
    node -e "require('../scripts/node/sonar-coverage.js').ws_to_mount('$1')"
  fi
}

function runEmberTestNoMockServer {
  testemPort=${TESTEM_PORTS_ARRAY[$RANDOM % ${#TESTEM_PORTS_ARRAY[@]} ]}

  local SHOULD_COVERAGE=$2
  if [ "${IS_MASTER_BUILD}" == "false" ]
  then
    # don't want to run coverage in a PR build no matter what
    SHOULD_COVERAGE="false"
  fi

  info "Running 'ember exam' for $1 on port $testemPort"
  info "COVERAGE=$SHOULD_COVERAGE NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF node --max_old_space_size=$3 ./node_modules/.bin/ember exam --split=4 --parallel --test-port $testemPort"
  COVERAGE=$SHOULD_COVERAGE NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF node --max_old_space_size=$3 ./node_modules/.bin/ember exam --split=4 --parallel --test-port $testemPort
  checkError "Ember exam/test failed for $1"
  success "'ember exam' for $1 was successful"
  # Push the newly generated coverage directory to the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
  if [ "${IS_MASTER_BUILD}" == "true" ]
  then
    # Merge tmp coverage directories to create a single coverage direcory
    ember coverage-merge
    info "Copying the coverage directory for '$1' from workspace to mount."
    node -e "require('../scripts/node/sonar-coverage.js').ws_to_mount('$1')"
  fi
}

function runEmberBuild {
  info "Running 'ember build' for $1"
  NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF node --max_old_space_size=$2 ./node_modules/.bin/ember build -e production
  checkError "Ember build failed for $1"
  success "'ember build' for $1 was successful"
}

# $1 = name of app
# $2 = whether or not `ember build` is necessary, it is necessary
#      when the app is deployable
# $3 = whether or not a mock server is needed
# $4 = Used for coverage.
# $5 = memory required to run

function buildEmberApp {

  cd $1

  ln -s ../node_modules node_modules

  local shouldTestApp=$(doTestApp $1)
  if [[ "$shouldTestApp" == "false" ]]
  then
    info "No reason to test $1, skipping it"
    # Sonar displays zero percent coverage for modules that are skipped since tests are not run to generage lcov coverage files
    # Pull in saved lcov files from the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
    if [ "${IS_MASTER_BUILD}" == "true" ]
    then
      info "Copying the coverage directory for '$1' from mount to workspace."
      node -e "require('../scripts/node/sonar-coverage.js').mount_to_ws('$1')"
    fi
  else

    info "Running tests for app: $1"

    # 'ember test'
    if [ "$3" = true ]
    then
      info '1 running tests with mock server'
      runEmberTestWithMockServer $1 $4 $5
      info '2 running tests with mock server done'
    else
      info '3 running tests with no mock server'
      runEmberTestNoMockServer $1 $4 $5
    fi

    # 'ember build' when running full build
    if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
    then
      # do not build apps that do not need `ember build`` run
      if [ "$2" = true ]
      then
        runEmberBuild $1 $5
      fi
    fi

    success "$1 is good!"

    if [[ "$1" == "style-guide" ]]
    then
      #### Deploy style guide to host if running full build
      if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
      then
        rm -rf /mnt/libhq-SA/SAStyle/production/*
        # hosted here: https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/production/
        cp -r dist/* /mnt/libhq-SA/SAStyle/production/
        success "Hosted style guide has been updated"
      fi
    fi
  fi

  cd $CWD
}

function buildMockServer {
  cd mock-server

  yarn unlink

  setWebProxy
  yarn
  unsetWebProxy

  # Run eslint/tests on mock-server code
  local shouldTestApp=$(doTestApp mock-server)
  if [[ "$shouldTestApp" == "false" ]]
  then
    info "No reason to test mock-server, skipping it"
  else
    info "Running ESLint on mock-server"
    yarn run eslint
    checkError "ESLint failed for mock-server"
    info "Running mock-server tests"
    mockServerTestPort=${MOCK_SERVER_PORTS_ARRAY[$RANDOM % ${#MOCK_SERVER_PORTS_ARRAY[@]} ]}
    RESPONSE_DELAY=1 MOCK_PORT=$mockServerTestPort yarn test
    checkError "mock-server tests failed"
    success "mock-server tests passed"
  fi

  yarn link

  cd $CWD
}

# $1 = name of library
function buildLibrary {
  cd $1

  ln -s ../node_modules node_modules

  info "Running tests for library: $1"

  npm run test

  checkError "test build failed for $1"
  success "test build for $1 was successful"

  cd $CWD
}

info "***********************"
info "Building apps"

yarn unlink mock-server

# install node scripts deps
info "Running install for node build utilities"
cd scripts/node
yarn

# install all UI depedencies
info "Running install for all application node packages"
cd ../..
yarn
cd $CWD

buildMockServer

# link in mock-server to root node_modules
yarn link mock-server

. ./scripts/jenkins/ngcoreui.sh
buildEmberApp streaming-data false true true 512
buildEmberApp component-lib false false true 512
buildEmberApp packager false true true 1024
buildEmberApp endpoint-rar false true true 1024
buildEmberApp entity-details false true true 512
buildEmberApp rsa-dashboard false true true 512
buildEmberApp license false true true 512
buildEmberApp rsa-context-menu false false true 512
buildEmberApp rsa-data-filters false false true 512
buildEmberApp recon false true true 512
buildEmberApp context false true true 512
buildEmberApp investigate-shared false false true 512
buildEmberApp preferences false true true 512
buildEmberApp test-helpers false false false 512
buildEmberApp style-guide true false false 512
buildEmberApp investigate-events false true true 1024
buildEmberApp investigate-hosts false true true 1024
buildEmberApp investigate-files false true true 1024
buildEmberApp investigate-users false true true 1024
buildEmberApp investigate false true true 4096
buildEmberApp respond-shared false true true 512
buildEmberApp rsa-list-manager false false true 512
buildEmberApp respond false true true 1024
buildEmberApp configure false true true 1024
buildEmberApp investigate-process-analysis false true true 1024
buildEmberApp admin-source-management false true true 1024
buildEmberApp admin false true true 1024
buildEmberApp springboard false true true 1024
buildEmberApp sa true true true 9192

success "All apps built successfully"

info "*** generating deprecation report"
cd scripts/node
node deprecation-report.js $BUILD_URL -w
info "*** end deprecation report"

info "*** performing deprecation check"
node deprecation-check.js $SKIP_DEPRECATION_CHECK
checkError "New deprecations have been added, bad developer"

cd $CWD

info "***********************"