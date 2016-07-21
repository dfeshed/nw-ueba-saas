#### UI Application build environment setup ####
#
# 1) Installs correct version of node.js if it needs
#  installing
# 2) Adds node to the PATH
# 3) Installs correct version of ember-cli using NPM
# 4) Installs correct version of bower using NPM
#

# Creates directory if it needs creating
function createDirectoryIfNecessary {
  local hasPath=$(pathExists $1)
  if [ "$hasPath" == "false" ]
  then
    mkdir $1
    info "Created $1"
  fi
}

# Checks version for binary
#
# $1, name of item
# $2, location of binary to run --version against
# $3, version that is target
function hasVersion {
  if [[ $($2 --version) =~ "$3" ]]
  then
    echo "true"
  else
    info "Version for $1 ${RED}does not match desired version${NC}."
    echo "false"
  fi
}

# Installs/wget web item, untars it and removes tarball
# If desired item exists, does nothing
#
# $1, location where binary should be
# $2, location of root of installed item
# $3, location of item to install, is retrieved via wget
# $4, destination of installed item
# $5, name of thing being installed
function installNodeTarballIfNecessary {
  # Check to see if item is present
  local hasPath=$(pathExists $NODE_BINARY)
  if [[ ("$hasPath" == "true") && ("$(hasVersion "node.js" $NODE_BINARY $NODE_VERSION)" == "true") ]]
  then
    info "node.js binary exists at $NODE_BINARY"
  else
    info "Installing node.js"
    createDirectoryIfNecessary $NODE_ROOT
    wget $NODE_DOWNLOAD_SOURCE -O $NODE_DOWNLOAD_DEST
    checkError "Wget of $NODE_DOWNLOAD_SOURCE  failed"
    info "Downloaded node.js tarball from $NODE_DOWNLOAD_DEST"
    tar xf $NODE_DOWNLOAD_DEST -C $NODE_ROOT --strip-components=1
    info "node.js extracted to $NODE_ROOT"
    rm $NODE_DOWNLOAD_DEST
    info "Removed tarball $NODE_DOWNLOAD_DEST"
  fi
}

# Will install an NPM library
# if it doesn't exist or if
# the correct version is not present
#
# $1, NPM name for library
# $2, executable name for library (can be different than NPM name, ember vs ember-cli)
# $3, path to command
# $4, desired version
function installNPMLibraryIfNecessary {
  local hasPath=$(pathExists $3)
  if [[ ("$hasPath" == "true") && ("$(hasVersion $1 $2 $4)" == "true") ]]
  then
    success "$1 version is: $4"
  else
    info "Installing $1"
    $NPM_BINARY --loglevel=silent install -g $1@$4
    checkError "Could not install $1"
    success "Installed $1 $4"
  fi
}

info "***********************"
info "Beginning environment setup"

echo -e "Verifying environment for:\nnode.js: $NODE_VERSION\nember-cli: $EMBER_CLI_VERSION\nbower: $BOWER_VERSION"

createDirectoryIfNecessary $LOCAL_CACHE_ROOT

setWebProxy
setHttpsProxy

# install node.js/NPM
installNodeTarballIfNecessary
success "node.js version is: $($NODE_BINARY --version)"
success "NPM version is: $($NPM_BINARY --version)"

# Set PATH for run to use correct npm/node
MOD_PATH_FILE=~/modpath.sh
info "Node was $(which node)"
echo "export PATH=$BINARIES:$PATH" > $MOD_PATH_FILE
source $MOD_PATH_FILE
rm $MOD_PATH_FILE
info "Node is now $(which node)"

# install NPM libraries if necessary
installNPMLibraryIfNecessary "bower" "bower" $BOWER_COMMAND $BOWER_VERSION
installNPMLibraryIfNecessary "ember-cli" "ember" $EMBER_CLI_COMMAND $EMBER_CLI_VERSION

unsetWebProxy
unsetHttpsProxy

success "Environment setup complete"
info "***********************"