# Convienence script to setup the environment
# sets up nvm, node, bower, ember-cli

# Software versions kept in external file for repeated use
# this brings the following variables in
# NODE_VERSION 
# EMBER_CLI_VERSION 
# BOWER_VERSION
scriptDir="$(dirname "$0")"
source $scriptDir/versions
source $scriptDir/_util.sh

echo -e "\nInstalling the following libraries:\nnode.js: $NODE_VERSION\nember-cli: $EMBER_CLI_VERSION\nbower: $BOWER_VERSION\n"

function hasVersion {
  if [[ $($1 --version) =~ "$2" ]]
  then
    echo "true"
  else
    echo "false"
  fi
}

# Checks for NVM global created in .bash_profile
# if its not there, NVM hasn't been installed, so install it
if [ -z ${NVM_DIR+x} ]
then
  info "Installing NVM"
  curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.31.2/install.sh | bash
  success "NVM installed!"
else
  info "NVM already present, not installing"
fi

# source in nvm so it can be used in this script
source ~/.nvm/nvm.sh

# install the required node version and make that version the default
nvm install $NODE_VERSION
nvm alias default $NODE_VERSION

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

# Yay!
success "Initial setup complete!"
success "To use your new environment, please either 'source' in your bash/zsh profile (i.e. 'source ~/.bash_profile'') or open up a new terminal window."