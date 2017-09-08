# Convienence script to setup the environment
# sets up nvm, node, ember-cli

# Software versions kept in external file for repeated use
# this brings the following variables in
# NODE_VERSION
# YARN_VERSION
# EMBER_CLI_VERSION
# PHANTOMJS_VERSION
scriptDir="$(dirname "$0")"
source $scriptDir/versions
source $scriptDir/_util.sh

# Setting up git hook
ln -s -f ../../scripts/hooks/pre-commit $scriptDir/../.git/hooks/pre-commit

echo -e "\nInstalling the following libraries:\nnode.js: $NODE_VERSION\nYarn: $YARN_VERSION\nember-cli: $EMBER_CLI_VERSION\nphantomjs-prebuilt: $PHANTOMJS_VERSION\n"

function hasVersion {
  if [[ $($1 --version) =~ "$2" ]]
  then
    echo "true"
  else
    echo "false"
  fi
}

# At present, the script only supports OS X and Windows
# Check if the user is on OS X
if [ "$(uname)" == "Darwin" ]
then
  # Checks for NVM global created in .bash_profile
  # if its not there, NVM hasn't been installed, so install it
  if [ -z ${NVM_DIR+x} ]
  then
    info "Detected OS X, Installing NVM"
    curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.31.2/install.sh | bash
    success "NVM installed!"
  else
    info "NVM already present on OS X, not installing"
  fi
  # source in nvm so it can be used in this script
  source ~/.nvm/nvm.sh
  # install the required node version and make that version the default
  nvm install $NODE_VERSION
  nvm alias default $NODE_VERSION
# User is not on OS X, must be on Windows
else
  # Check if nvm-windows has been installed
  if [ -d "${HOME}/AppData/Roaming/nvm" ]
  then
    success "Detected NVM for Windows"
    # install the required node version and make that version the default
    nvm install $NODE_VERSION
    nvm use $NODE_VERSION
  else
    red "NVM is not installed on this Windows"
    info "Please go to https://github.com/coreybutler/nvm-windows/releases and install NVM"
    exit 1
  fi
fi

# $1 name of library
# $2 NPM name of library
# $3 CLI name for library
# $4 version of library
function installGlobalDependency {
  if [[ "$(hasVersion $3 $4)" == "false" ]]
  then
    info "Installing $1"
    npm install -g $2@$4
    success "$1 installed!"
  else
    info "Proper $1 version already installed"
  fi
}

installGlobalDependency "Yarn" "yarn" "yarn" $YARN_VERSION
installGlobalDependency "ember-cli" "ember-cli" "ember" $EMBER_CLI_VERSION

# install phantomjs-prebuilt if it hasn't been
if [[ "$(hasVersion "phantomjs" $PHANTOMJS_VERSION)" == "false" ]]
then
  info "Installing phantomjs-prebuilt"
  npm install -g phantomjs-prebuilt@$PHANTOMJS_PREBUILT_VERSION
  success "phantomjs-prebuilt installed!"
else
  info "Proper phantomjs-prebuilt version already installed"
fi

# Have to do this so that yarn doesn't go to registry.yarnpkg which causes
# tier2 permission issues. registry.yarnpkg is a reverse proxy placeholder
# in case the Yarn folks feel that improvements can be made on the other end
yarn config set registry https://registry.npmjs.org/

# Yay!
success "Initial setup complete!"