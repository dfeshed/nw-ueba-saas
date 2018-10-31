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
  echo $_need
}

# Turn strings into arrays
# cannot export arrays at command line
IFS=', ' read -r -a TESTEM_PORTS_ARRAY <<< "$TESTEM_PORTS"
IFS=', ' read -r -a MOCK_SERVER_PORTS_ARRAY <<< "$MOCK_SERVER_PORTS"

function runEmberTestWithMockServer {
  local mockPort=${MOCK_SERVER_PORTS_ARRAY[$RANDOM % ${#MOCK_SERVER_PORTS_ARRAY[@]} ]}
  local testemPort=${TESTEM_PORTS_ARRAY[$RANDOM % ${#TESTEM_PORTS_ARRAY[@]} ]}

  yarn link mock-server

  info "Starting Express mock test server for $1"

  # run mock server
  RESPONSE_DELAY=1 MOCK_PORT=$mockPort node mockserver.js &
  checkError "Mock server for $1 refused to start"
  local PID=$!
  success "$1 mock server started, process id: $PID"

  # now run the tests
  info "Running 'ember exam' for $1 on port $testemPort"
  info "COVERAGE=$2 NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF MOCK_PORT=$mockPort ember exam --split=4 --parallel --test-port $testemPort"
  COVERAGE=$2 NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF MOCK_PORT=$mockPort ember exam --split=4 --parallel --test-port $testemPort
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
  ember coverage-merge
  # Push the newly generated coverage directory to the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
  # TODO: Coverage is only enabled for master builds. Expand the functionality to PR builds later.
  if [ "${IS_MASTER_BUILD}" == "true" ]
  then
    info "Copying the coverage directory for '$1' from workspace to mount."
    node -e "require('../scripts/node/sonar-coverage.js').ws_to_mount('$1')"
  fi
}

function runEmberTestNoMockServer {
  testemPort=${TESTEM_PORTS_ARRAY[$RANDOM % ${#TESTEM_PORTS_ARRAY[@]} ]}
  info "Running 'ember exam' for $1 on port $testemPort"
  COVERAGE=$2 NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF ember exam --split=4 --parallel --test-port $testemPort
  checkError "Ember exam/test failed for $1"
  success "'ember exam' for $1 was successful"
  # Merge tmp coverage directories to create a single coverage direcory
  ember coverage-merge
  # Push the newly generated coverage directory to the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
  if [ "${IS_MASTER_BUILD}" == "true" ]
  then
    info "Copying the coverage directory for '$1' from workspace to mount."
    node -e "require('../scripts/node/sonar-coverage.js').ws_to_mount('$1')"
  fi
}

function runEmberBuild {
  info "Running 'ember build' for $1"
  NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF ember build -e production
  checkError "Ember build failed for $1"
  success "'ember build' for $1 was successful"
}

# $1 = name of app
# $2 = whether or not `ember build` is necessary, it is necessary
#      when the app is deployable
# $3 = whether or not a mock server is needed
# $4 = Used for coverage.

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
      runEmberTestWithMockServer $1 $4
    else
      runEmberTestNoMockServer $1 $4
    fi

    # 'ember build' when running full build
    if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
    then
      # do not build apps that do not need `ember build`` run
      if [ "$2" = true ]
      then
        runEmberBuild $1
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
. ./scripts/jenkins/direct-access.sh
buildEmberApp streaming-data false true true
buildEmberApp component-lib false false true
buildEmberApp packager false true true
buildEmberApp license false true true
buildEmberApp rsa-data-filters false true true
buildEmberApp recon false true true
buildEmberApp context false true true
buildEmberApp investigate-shared false false true
buildEmberApp preferences false true true
buildEmberApp test-helpers false false false
buildEmberApp ember-cli-document-title false false false
buildEmberApp style-guide true false false
buildEmberApp investigate-events false true true
buildEmberApp investigate-hosts false true true
buildEmberApp investigate-files false true true
buildEmberApp investigate-users false true true
buildEmberApp investigate false true true
buildEmberApp respond-shared false false true
buildEmberApp respond false true true
buildEmberApp configure false true true
buildEmberApp investigate-process-analysis false true true
buildEmberApp admin-source-management false true true
buildEmberApp admin false true true
buildEmberApp sa true true true
buildLibrary broccoli-theme

success "All apps built"
info "***********************"
