# This can be run to quickly NPM/Bower install
# all the dependencies for all the apps

function installYarnDeps {
  info "Using Yarn to install NPM dependencies for: $1"
  yarn
  checkError "Failed to install NPM dependencies for $1, try again, if this persists (it shouldn't) then get some help."
}

function installAppDeps {
  cd ../$1

  installYarnDeps $1

  info "Installing Bower dependencies for: $1"
  bower install
  checkError "Failed to install Bower dependencies for $1, try again, if this persists (it shouldn't) then get some help."

  yarn link mock-server

  success "$1 is ready to go!"
}

# source in nvm so it can be used
. ~/.nvm/nvm.sh

CWD=$(pwd)
scriptDir="$(dirname $0)"
. $scriptDir/_util.sh

# run install on node build node utilities
info "Running install for node build utilities"
cd $scriptDir/node
yarn
cd $CWD

# mock-server is just Yarn install
cd $scriptDir/../mock-server
installYarnDeps mock-server
yarn link

installAppDeps component-lib
installAppDeps streaming-data
installAppDeps recon
installAppDeps style-guide
installAppDeps sa

cd $CWD
