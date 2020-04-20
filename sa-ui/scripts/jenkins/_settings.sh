# All jenkins build globals/settings
scriptDir="$(dirname "$0")"
source $scriptDir/../versions

# save off current working directory
CWD=$(pwd)

# node settings
LOCAL_CACHE_ROOT=~/.node
NODE_ROOT=$LOCAL_CACHE_ROOT/node
BINARIES=$NODE_ROOT/bin
NODE_BINARY=$BINARIES/node
NODE_DOWNLOAD_DEST="$NODE_ROOT/node.tar.gz"
# Mac setup for testing locally
# NODE_DOWNLOAD_SOURCE="https://nodejs.org/dist/v$NODE_VERSION/node-v$NODE_VERSION-darwin-x64.tar.gz" # local
# jenkins setup
NODE_DOWNLOAD_SOURCE="https://nodejs.org/dist/v$NODE_VERSION/node-v$NODE_VERSION-linux-x64.tar.gz"

# NPM settings
NPM_BINARY=$BINARIES/npm
EMBER_CLI_COMMAND=$BINARIES/ember
YARN_COMMAND=$BINARIES/yarn

# RPM install folder
APP_INSTALL_FOLDER=/opt/rsa/nw-ui

# RPM settings
SA_ROOT=$CWD/sa
SA_RPM_ROOT=$SA_ROOT/rpm
TMP_RPM_BUILD_ROOT=$SA_RPM_ROOT/tmp-buildroot
RPM_BUILD_ROOT=$SA_RPM_ROOT/buildroot
RPM_SPEC_FILE=$SA_RPM_ROOT/SPECS/nw-ui.spec
