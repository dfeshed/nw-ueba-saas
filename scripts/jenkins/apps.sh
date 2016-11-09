PORTS=(7351 7352 7353 7354 7355 7356 7357 7358 7359 7360 7361 7362 7363 7364 7365 7366 7367 7368 7369 7370 7371 7372 7373 7374 7375 7376 7377 7378 7379 7380)
MOCK_PORTS=(9980 9981 9982 9983 9984 9985 9986 9987 9988 9989 9990 9991 9992 9993 9994 9995 9996 9997)

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
  local mockPort=${MOCK_PORTS[$RANDOM % ${#MOCK_PORTS[@]} ]}
  local testemPort=${PORTS[$RANDOM % ${#PORTS[@]} ]}

  info "Starting Express mock test server for $1"

  # run mock server
  cd tests/server
  MOCK_PORT=$mockPort node server.js &
  checkError "Mock server for $1 refused to start"
  local PID=$!
  success "$1 mock server started, process id: $PID"

  cd ../..

  # now run the tests
  info "Running 'ember test' for $1 on port $testemPort"
  MOCK_PORT=$mockPort ember test --test-port $testemPort
  local status=$?

  # kill mock server
  kill -9 $PID

  if [[ $status != "0" ]]
  then
    fail "$1"
    fail "Exiting..."
    exit $status
  fi

  success "'ember test' for $1 was successful"
}

function runEmberTestNoMockServer {
  testemPort=${PORTS[$RANDOM % ${#PORTS[@]} ]}
  info "Running 'ember test' for $1 on port $testemPort"
  ember test --test-port $testemPort
  checkError "Ember test failed for $1"
  success "'ember test' for $1 was successful"
}

function runEmberBuild {
  info "Running 'ember build' for $1"
  ember build -e production
  checkError "Ember build failed for $1"
  success "'ember build' for $1 was successful"
}

# $1 = name of app
# $2 = true if app needs mock server started
function buildEmberApp {
  info "Beginning build for app: $1"

  cd $1

  # install Yarn/NPM deps
  runAppYarnInstall $1

  yarn link mock-server

  # install Bower deps
  runAppBowerInstall $1

  # 'ember test'
  if [ "$2" = true ]
  then
    runEmberTestWithMockServer $1
  else
    runEmberTestNoMockServer $1
  fi

  # 'ember build' when running full build
  if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
  then
    runEmberBuild $1
  fi

  success "$1 is good!"

  cd $CWD
}

info "***********************"
info "Building apps"

# Run node script that will check bower versions for all projects
node scripts/jenkins/check-bower-versions.js

setWebProxy

# Yarn install and run eslint on mock-server code
cd mock-server
runAppYarnInstall mock-server
info "Running ESLint on mock-server"
yarn run eslint
checkError "ESLint failed for mock-server"
success "mock-server ESLint success"
yarn link
cd $CWD

# http://stackoverflow.com/questions/21789683/how-to-fix-bower-ecmderr
# fixes ecmderr with bower install
git config --global url."https://".insteadOf git://

buildEmberApp streaming-data true

buildEmberApp component-lib
buildEmberApp recon true
buildEmberApp style-guide

#### Deploy style guide to host if running full build
if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
then
  rm -rf /mnt/libhq-SA/SAStyle/production/*
  # hosted here: https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/production/
  cp -r style-guide/dist/* /mnt/libhq-SA/SAStyle/production/
  success "Hosted style guide has been updated"
fi

buildEmberApp sa

unsetWebProxy

success "All apps built"
info "***********************"
