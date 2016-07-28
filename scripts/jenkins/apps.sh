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

function runAppNPMInstall {
  info "Running 'npm install' for $1"
  $NPM_BINARY --prefix $1/ i
  checkError "NPM install failed for $1"
  success "Installed $1 NPM dependencies"
}

function runAppBowerInstall {
  info "Running 'bower install'' for $1"
  bower install \
  --config.cwd=$1 \
  --offline \
  --config.storage.registry=$BOWER_REGISTRY/bower_repository/registry \
  --config.storage.packages=$BOWER_REGISTRY/bower_repository/packages
  success "Installed $1 bower dependencies"
}

function runEmberTest {
  info "Running 'ember test' for $1 on port $2"
  cd $1
  ember test --test-port $2
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
  runEmberTest $1 $2

  # 'ember build' when running full build
  if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
  then
    runEmberBuild $1
  fi

  success "$1 is good!"
}

function handleBowerArchive {
  info "Retrieving bower archive from artifactory"

  # get latest snapshot of bower archive from artifactory
  wget -O $BOWER_ARCHIVE_JAR http://repo1.rsa.lab.emc.com:8081/artifactory/asoc-snapshots/com/rsa/asoc/netwitness/ui/bower-registry/$NETWITNESS_VERSION-SNAPSHOT/bower-registry-$NETWITNESS_VERSION-SNAPSHOT.jar

  # remove old bower registry, create new one with artifact unzip and cleanup the jar file
  rm -rf $BOWER_REGISTRY
  mkdir $BOWER_REGISTRY
  cd $BOWER_REGISTRY
  jar xvf ../$BOWER_ARCHIVE_JAR
  cd $CWD
  rm -rf $BOWER_ARCHIVE_JAR

  info "Unzipped bower archive to $BOWER_REGISTRY"
  success "Bower archive ready"
}

info "***********************"
info "Building apps"

handleBowerArchive

# doing this after bower archive has been retrieved as
# setting this before that will break artifactory fetch
setWebProxy

buildEmberApp component-lib 7355
buildEmberApp style-guide 7356

#### Deploy style guide to host if running full build
if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
then
  rm -rf /mnt/libhq-SA/SAStyle/production/*
  cp -r style-guide/dist/* /mnt/libhq-SA/SAStyle/production/
  success "Hosted style guide has been updated"
fi

buildEmberApp sa 7357

unsetWebProxy

success "All apps built"
info "***********************"

