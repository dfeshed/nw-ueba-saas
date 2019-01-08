#!/bin/bash
function runEmberTestDirectAccessMockServer {
  local testemPort=${TESTEM_PORTS_ARRAY[$RANDOM % ${#TESTEM_PORTS_ARRAY[@]} ]}

  info "Starting direct access mock test server"

  # run mock server
  node ./da-mock-server/index.js &
  checkError "Direct access mock server refused to start"
  local PID=$!
  success "Direct access mock server started, process id: $PID"

  # now run the tests
  info "Running 'ember exam' for direct-access on port $testemPort"
  COVERAGE=$2 NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF ember exam --split=4 --parallel --test-port $testemPort
  local status=$?

  # kill mock server
  kill -9 $PID

  if [[ $status != "0" ]]
  then
    fail "direct-access"
    fail "Exiting..."
    exit $status
  fi
  checkError "Ember exam/test failed for direct-access"
  success "'ember exam' for direct-access was successful"
  # Merge tmp coverage directories to create a single coverage direcory
  ember coverage-merge
  # Push the newly generated coverage directory to the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
  if [ "${IS_MASTER_BUILD}" == "true" ]
  then
    info "Copying the coverage directory for 'direct-access' from workspace to mount."
    node -e "require('../scripts/node/sonar-coverage.js').ws_to_mount('direct-access')"
  fi
}

function buildDirectAccess {
  cd direct-access

  ln -s ../node_modules node_modules

  buildDirectAccessMockServer

  local shouldTestApp=$(doTestApp direct-access)
  if [[ "$shouldTestApp" == "false" ]]
  then
    info "No reason to test direct-access, skipping it"
    # Sonar displays zero percent coverage for modules that are skipped since tests are not run to generage lcov coverage files
    # Pull in saved lcov files from the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
    if [ "${IS_MASTER_BUILD}" == "true" ]
    then
      info "Copying the coverage directory for 'direct-access' from mount to workspace."
      node -e "require('../scripts/node/sonar-coverage.js').mount_to_ws('direct-access')"
    fi
  else

    info "Running tests for app: direct-access"

    runEmberTestDirectAccessMockServer direct-access $4

    # 'ember build' when running full build
    if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
    then
      # do not build apps that do not need `ember build`` run
      if [ "$2" = true ]
      then
        runEmberBuild direct-access
      fi
    fi

    success "direct-access is good!"

    #### Deploy direct-access to host if running full build
    if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
    then
      rm -rf /mnt/libhq-SA/SAStyle/direct-access/*
      version="$(grep -Po '(?<="version": ")[^"]*' package.json)"
      buildno="0"
      # tarball lives here: https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/direct-access/direct-access-$version.tar.gz
      mkdir -p /mnt/libhq-SA/SAStyle/direct-access && tar -cyvf /mnt/libhq-SA/SAStyle/direct-access/ngcoreui-${version}.nw.${buildno}.any.tar.bz2 dist/*
      success "Hosted direct-access app has been updated"
    fi
  fi

  cd $CWD
}

function buildDirectAccessMockServer {
  cd da-mock-server

  yarn

  cd ..
}

buildDirectAccess direct-access true true true
