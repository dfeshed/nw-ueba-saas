# Convienence script to setup the environment
# sets up nvm, node, bower, ember-cli

# Software versions kept in external file for repeated use
# this brings the following variables in
# NODE_VERSION
# EMBER_CLI_VERSION
# BOWER_VERSION
# PHANTOMJS_VERSION
scriptDir="$(dirname "$0")"
source $scriptDir/versions
source $scriptDir/_util.sh

echo -e "\nInstalling the following libraries:\nnode.js: $NODE_VERSION\nember-cli: $EMBER_CLI_VERSION\nbower: $BOWER_VERSION\nphantomjs-prebuilt: $PHANTOMJS_VERSION\n"

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

# install ember if it hasn't been
if [[ "$(hasVersion "ember" $EMBER_CLI_VERSION)" == "false" ]]
then
  info "Installing ember-cli"
  npm install -g ember-cli@$EMBER_CLI_VERSION
  success "ember-cli installed!"
else
  info "Proper ember-cli version already installed"
fi

# install bower if it hasn't been
if [[ "$(hasVersion "bower" $BOWER_VERSION)" == "false" ]]
then
  info "Installing bower"
  npm install -g bower@$BOWER_VERSION
  success "Bower installed!"
else
  info "Proper bower version already installed"
fi

# install phantomjs-prebuilt if it hasn't been
if [[ "$(hasVersion "phantomjs" $PHANTOMJS_VERSION)" == "false" ]]
then
  info "Installing phantomjs-prebuilt"
  npm install -g phantomjs-prebuilt@$PHANTOMJS_VERSION
  success "phantomjs-prebuilt installed!"
else
  info "Proper phantomjs-prebuilt version already installed"
fi

# Yay!
success "Initial setup complete!"