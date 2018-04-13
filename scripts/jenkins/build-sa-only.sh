#### Run Ember build without testing each component
#
# 1) Will ensure node/npm, yarn, and ember-cli are
#   installed and with the correct version
# 2) Builds sa
# 3) Builds RPM for SA
# 4) Optionally Puts RPM into YUM repos
# 5) Optionally builds docker image
# 6) Optionally publishes docker image to DTR
#

#### INPUTS
# export FF_ON=future
#   Sets web application features to true. To set multiple features, comma-delimit
#   feature names.
# export FF_OFF=future
#   Sets web application features to false. To set multiple features, comma-delimit
#   feature names.
# export BUILD_DOCKER_IMAGE=true
#   Whether or not to build a docker image
# export PUBLISH_DOCKER_IMAGE=true
#   Whether or not to publish an RPM
# export PUBLISH_RPM=true
#   Whether or not to publish an RPM

# turns off noisy jenkins output
# comment this out to debug problematic build
set +x

# bring in utilities and settings
scriptDir="$(dirname "$0")"
. $scriptDir/../_util.sh
. $scriptDir/_settings.sh

#### Setup environment ####
. $scriptDir/environment.sh

yarn

cd sa
ln -s ../node_modules node_modules
info "Running 'ember build' for sa"
NODE_ENV=production FF_ON=$FF_ON FF_OFF=$FF_OFF ember build -e production
checkError "Ember build failed for sa"
success "'ember build' for sa was successful"

cd $CWD

#### Build RPM
. $scriptDir/rpm.sh

#### If selected, build Docker imaages and push to DTR
if [ "${BUILD_DOCKER_IMAGE}" == "true" ]
then
  . $scriptDir/build-docker-image.sh
fi

#### Move RPM to Yum directory
if [ "$PUBLISH_RPM" == "true" ]
then
  . $scriptDir/yum.sh
fi

#### Victory, poppin bottles
success "Finished Build!"
