#!/bin/bash
function runEmberTestNgcoreuiMockServer {
  local testemPort=${TESTEM_PORTS_ARRAY[$RANDOM % ${#TESTEM_PORTS_ARRAY[@]} ]}

  info "Starting ngcoreui mock test server"

  # run mock server
  node ./ngcoreui-mock-server/index.js &
  checkError "ngcoreui mock server refused to start"
  local PID=$!
  success "ngcoreui mock server started, process id: $PID"

  # now run the tests
  info "Running 'ember exam' for ngcoreui on port $testemPort"
  COVERAGE=$2 NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF ember exam --split=4 --parallel --test-port $testemPort
  local status=$?

  # kill mock server
  kill -9 $PID

  if [[ $status != "0" ]]
  then
    fail "ngcoreui"
    fail "Exiting..."
    exit $status
  fi
  checkError "Ember exam/test failed for ngcoreui"
  success "'ember exam' for ngcoreui was successful"
  # Merge tmp coverage directories to create a single coverage direcory
  ember coverage-merge
  # Push the newly generated coverage directory to the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
  if [ "${IS_MASTER_BUILD}" == "true" ]
  then
    info "Copying the coverage directory for 'ngcoreui' from workspace to mount."
    node -e "require('../scripts/node/sonar-coverage.js').ws_to_mount('ngcoreui')"
  fi
}

function buildNgcoreui {
  cd ngcoreui

  ln -s ../node_modules node_modules

  buildNgcoreuiMockServer

  local shouldTestApp=$(doTestApp ngcoreui)
  if [[ "$shouldTestApp" == "false" ]]
  then
    info "No reason to test ngcoreui, skipping it"
    # Sonar displays zero percent coverage for modules that are skipped since tests are not run to generage lcov coverage files
    # Pull in saved lcov files from the mount '/mnt/libhq-SA/SAStyle/sa-ui-coverage/<submodule>/coverage/*';
    if [ "${IS_MASTER_BUILD}" == "true" ]
    then
      info "Copying the coverage directory for 'ngcoreui' from mount to workspace."
      node -e "require('../scripts/node/sonar-coverage.js').mount_to_ws('ngcoreui')"
    fi
  else

    info "Running tests for app: ngcoreui"

    runEmberTestNgcoreuiMockServer ngcoreui $4

    # 'ember build' when running full build
    if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
    then
      # do not build apps that do not need `ember build`` run
      if [ "$2" = true ]
      then
        runEmberBuild ngcoreui
      fi
    fi

    success "ngcoreui is good!"

    #### Deploy ngcoreui to host if running full build
    if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
    then
      rm -rf /mnt/libhq-SA/SAStyle/ngcoreui/*
      version="$(grep -Po '(?<="version": ")[^"]*' package.json)"
      revision="$(git rev-list --count HEAD)"
      # tarball lives here: https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/ngcoreui/
      mkdir -p /mnt/libhq-SA/SAStyle/ngcoreui && tar -cjvf /mnt/libhq-SA/SAStyle/ngcoreui/ngcoreui-${version}.nw.${revision}.any.tar.bz2 dist/*
      success "Hosted ngcoreui app has been updated"
    fi
  fi

  cd $CWD
}

function buildNgcoreuiMockServer {
  cd ngcoreui-mock-server

  yarn

  cd ..
}

buildNgcoreui ngcoreui true true true
