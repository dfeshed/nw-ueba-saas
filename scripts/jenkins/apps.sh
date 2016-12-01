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

# Turn strings into arrays
# cannot export arrays at command line
IFS=', ' read -r -a TESTEM_PORTS_ARRAY <<< "$TESTEM_PORTS"
IFS=', ' read -r -a MOCK_SERVER_PORTS_ARRAY <<< "$MOCK_SERVER_PORTS"

function runAppYarnInstall {
  info "Running 'yarn' for $1"
  yarn
  checkError "Yarn install failed for $1"
  success "Installed $1 NPM dependencies"
}

function runAppBowerInstall {
  info "Running 'bower install'' for $1"
  bower install
  checkError "Bower install failed for $1"
  success "Installed $1 bower dependencies"
}

function runEmberTestWithMockServer {
  local mockPort=${MOCK_SERVER_PORTS_ARRAY[$RANDOM % ${#MOCK_SERVER_PORTS_ARRAY[@]} ]}
  local testemPort=${TESTEM_PORTS_ARRAY[$RANDOM % ${#TESTEM_PORTS_ARRAY[@]} ]}

  info "Starting Express mock test server for $1"

  # run mock server
  cd tests/server
  MOCK_PORT=$mockPort node server.js &
  checkError "Mock server for $1 refused to start"
  local PID=$!
  success "$1 mock server started, process id: $PID"

  cd ../..

  # now run the tests
  info "Running 'ember exam' for $1 on port $testemPort"
  MOCK_PORT=$mockPort ember exam --split=4 --parallel --test-port $testemPort
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
}

function runEmberTestNoMockServer {
  testemPort=${TESTEM_PORTS_ARRAY[$RANDOM % ${#TESTEM_PORTS_ARRAY[@]} ]}
  info "Running 'ember exam' for $1 on port $testemPort"
  ember exam --split=4 --parallel --test-port $testemPort
  checkError "Ember exam/test failed for $1"
  success "'ember exam' for $1 was successful"
}

function runEmberBuild {
  info "Running 'ember build' for $1"
  ember build -e production
  checkError "Ember build failed for $1"
  success "'ember build' for $1 was successful"
}

# $1 = name of app
# $2 = whether or not `ember build` is necessary
# $3 = whether or not a mock server is needed
function buildEmberApp {
  info "Beginning build for app: $1"

  cd $1

  # install Yarn/NPM deps
  runAppYarnInstall $1

  yarn link mock-server

  # install Bower deps
  runAppBowerInstall $1

  # 'ember test'
  if [ "$3" = true ]
  then
    runEmberTestWithMockServer $1
  else
    runEmberTestNoMockServer $1
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

  cd $CWD
}

info "***********************"
info "Building apps"

# Run node script that will check bower versions for all projects
node scripts/node/check-bower-versions.js

# install node scripts deps
cd scripts/node
yarn
cd $CWD

setWebProxy

# Yarn install and run eslint/tests on mock-server code
cd mock-server
runAppYarnInstall mock-server
info "Running ESLint on mock-server"
yarn run eslint
checkError "ESLint failed for mock-server"
info "Running mock-server tests"
mockServerTestPort=${MOCK_SERVER_PORTS_ARRAY[$RANDOM % ${#MOCK_SERVER_PORTS_ARRAY[@]} ]}
MOCK_PORT=$mockServerTestPort yarn test
checkError "mock-server tests failed"
success "mock-server tests passed"
yarn link
cd $CWD

# http://stackoverflow.com/questions/21789683/how-to-fix-bower-ecmderr
# fixes ecmderr with bower install
git config --global url."https://".insteadOf git://

buildEmberApp streaming-data false true

buildEmberApp component-lib false
buildEmberApp recon false true
buildEmberApp style-guide true

#### Deploy style guide to host if running full build
if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
then
  rm -rf /mnt/libhq-SA/SAStyle/production/*
  # hosted here: https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/production/
  cp -r style-guide/dist/* /mnt/libhq-SA/SAStyle/production/
  success "Hosted style guide has been updated"
fi

buildEmberApp sa true

unsetWebProxy

success "All apps built"
info "***********************"
