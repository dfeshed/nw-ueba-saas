#### Build Ember Apps ####
#
# 1) Will wget and install the bower archive
#   from artifactory.
# 2) component-lib: NPM install + Bower install + ember test + ember build
# 3) style-guide: NPM install + Bower install + ember test + ember build
# 4) sa: NPM install + Bower install + ember test + ember build
# 5) copies style-guide out to hosted directory
#    (https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/production/)
#

PORTS=(7351 7352 7353 7354 7355 7356 7357 7358 7359 7360 7361 7362 7363 7364 7365 7366 7367 7368 7369 7370 7371 7372 7373 7374 7375 7376 7377 7378 7379 7380)

function runAppNPMInstall {
  info "Running 'npm install' for $1"
  $NPM_BINARY --prefix $1/ i
  checkError "NPM install failed for $1"
  success "Installed $1 NPM dependencies"
}

function runAppBowerInstall {
  info "Running 'bower install'' for $1"
  bower install --config.cwd=$1
  checkError "Bower install failed for $1"
  success "Installed $1 bower dependencies"
}

function runEmberTest {
  port=${PORTS[$RANDOM % ${#PORTS[@]} ]}
  info "Running 'ember test' for $1 on port $port"
  cd $1
  ember test --test-port $port
  checkError "Ember test failed for $1"
  success "'ember test' for $1 was successful"
  cd $CWD
}

function runEmberBuild {
  info "Running 'ember build' for $1"
  cd $1
  ember build -e production
  checkError "Ember build failed for $1"
  success "'ember build' for $1 was successful"
  cd $CWD
}

function buildEmberApp {
  info "Beginning build for app: $1"

  # install NPM deps
  runAppNPMInstall $1

  # install Bower deps
  runAppBowerInstall $1

  # 'ember test'
  runEmberTest $1

  # 'ember build' when running full build
  if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
  then
    runEmberBuild $1
  fi

  success "$1 is good!"
}

info "***********************"
info "Building apps"

# Run node script that will check bower versions for all projects
node scripts/jenkins/check-bower-versions.js

setWebProxy

# http://stackoverflow.com/questions/21789683/how-to-fix-bower-ecmderr
# fixes ecmderr with bower install
git config --global url."https://".insteadOf git://

buildEmberApp component-lib
buildEmberApp style-guide

#### Deploy style guide to host if running full build
if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
then
  rm -rf /mnt/libhq-SA/SAStyle/production/*
  cp -r style-guide/dist/* /mnt/libhq-SA/SAStyle/production/
  success "Hosted style guide has been updated"
fi

buildEmberApp sa

unsetWebProxy

success "All apps built"
info "***********************"

