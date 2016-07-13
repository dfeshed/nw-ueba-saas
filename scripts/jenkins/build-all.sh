#### Run Entire Build
#
# 1) Will ensure node/npm, bower and ember-cli are
# installed and with the correct version
# 2) Will then run builds for the 3 ember apps
#   which includes tests and production builds
#   and re-hosting of style-guide
# 3) Builds RPM for SA
# 4) Puts RPM into YUM repos
#

# For testing purposes
# temp clean up while setting up script
# rm -rf ~/.node

# turns off noisy jenkins output
# possibly comment this out to debug problematic build
set +x

# bring in utilities and settings
scriptDir="$(dirname "$0")"
. $scriptDir/../_util.sh
. $scriptDir/_settings.sh

#### Setup environment ####
. $scriptDir/environment.sh

#### Build Apps ####
. $scriptDir/apps.sh

#### Build RPM
. $scriptDir/rpm.sh

#### Moving RPM to Yum directory
. $scriptDir/yum.sh

#### Victory, poppin bottles
success "Finished Build!"